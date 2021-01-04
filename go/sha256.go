package main

import (
	"fmt"
	"os"
)

var k = [64]uint32{
	0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
	0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
	0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
	0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
	0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
	0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
	0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
	0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2}

func main() {
	if len(os.Args) < 1 {
		fmt.Println("Please provide a string to hash")
	}
	s := os.Args[1]
	fmt.Println(Digest(&s))
}

func pad(s *string) []bool {
	l := len(*s) * 8
	k := 0
	var M []bool

	for _, letter := range *s {
		for j := 7; j >= 0; j-- {
			M = append(M, ((letter>>(j))&1) == 1)
		}
	}

	M = append(M, true)

	for (l+1+k)%512 != 448 {
		M = append(M, false)
		k++
	}

	len64 := uint64(l)
	for j := 63; j >= 0; j-- {
		M = append(M, ((len64>>j)&1) == 1)
	}
	return M
}

func toBlocks(b []bool) [][16]uint32 {
	var blocks [][16]uint32
	var block [16]uint32
	var word uint32
	m := 0
	for i, val := range b {
		if val {
			word |= 1
		}
		if (i+1)%32 == 0 {
			block[m] = word
			m++
			if m == 16 {
				blocks = append(blocks, block)
				m = 0
			}
		}
		word <<= 1
	}

	return blocks
}

//Digest  Generates a sha-256 digest
func Digest(s *string) string {
	hashString := ""
	paddedMessage := pad(s)
	var M = toBlocks(paddedMessage)
	N := len(M)
	var hash = [][8]uint32{{
		0x6a09e667,
		0xbb67ae85,
		0x3c6ef372,
		0xa54ff53a,
		0x510e527f,
		0x9b05688c,
		0x1f83d9ab,
		0x5be0cd19}}
	var interHash [8]uint32
	var T1 uint32
	var T2 uint32
	var W [64]uint32
	var a, b, c, d, e, f, g, h uint32
	for i := 1; i <= N; i++ {
		a = hash[i-1][0]
		b = hash[i-1][1]
		c = hash[i-1][2]
		d = hash[i-1][3]
		e = hash[i-1][4]
		f = hash[i-1][5]
		g = hash[i-1][6]
		h = hash[i-1][7]

		for j := 0; j < 64; j++ {
			if j < 16 {
				W[j] = M[i-1][j]
			} else {
				W[j] = add(sigma1(W[j-2]), W[j-7], sigma0(W[j-15]), W[j-16])
			}
			T1 = add(h, sum1(e), ch(e, f, g), k[j], W[j])
			T2 = add(sum0(a), maj(a, b, c))

			h = g
			g = f
			f = e
			e = add(d, T1)
			d = c
			c = b
			b = a
			a = add(T1, T2)
		}
		interHash[0] = add(a, hash[i-1][0])
		interHash[1] = add(b, hash[i-1][1])
		interHash[2] = add(c, hash[i-1][2])
		interHash[3] = add(d, hash[i-1][3])
		interHash[4] = add(e, hash[i-1][4])
		interHash[5] = add(f, hash[i-1][5])
		interHash[6] = add(g, hash[i-1][6])
		interHash[7] = add(h, hash[i-1][7])
		hash = append(hash, interHash)

	}
	for _, val := range hash[len(hash)-1] {
		hashString += fmt.Sprintf("%x", val)
	}

	return hashString
}

func rotR(A, n uint32) uint32 {
	mask := uint32(0xffffffff & ((1 << n) - 1))
	return (A&mask)<<(32-n) | (A >> n)
}

func not(x uint32) uint32 {
	return x ^ 0xffffffff
}

func add(nums ...uint32) uint32 {
	var sum uint64 = 0
	for _, n := range nums {
		sum += uint64(n)
	}
	return uint32(sum & 0xffffffff)
}

func ch(x, y, z uint32) uint32 {
	return (x & y) ^ (not(x) & z)
}

func maj(x, y, z uint32) uint32 {
	return (x & y) ^ (x & z) ^ (y & z)
}

func sum0(x uint32) uint32 {
	return rotR(x, 2) ^ rotR(x, 13) ^ rotR(x, 22)
}

func sum1(x uint32) uint32 {
	return rotR(x, 6) ^ rotR(x, 11) ^ rotR(x, 25)
}

func sigma0(x uint32) uint32 {
	return rotR(x, 7) ^ rotR(x, 18) ^ (x >> 3)
}

func sigma1(x uint32) uint32 {
	return rotR(x, 17) ^ rotR(x, 19) ^ (x >> 10)
}
