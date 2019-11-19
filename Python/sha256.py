K = (0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
     0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
     0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
     0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
     0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
     0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
     0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
     0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2)


def digest(msg):
    paddedMessage = padMessage(msg)
    M = parseMessage(paddedMessage)

    N = len(M)
    H = [[
        0x6a09e667,
        0xbb67ae85,
        0x3c6ef372,
        0xa54ff53a,
        0x510e527f,
        0x9b05688c,
        0x1f83d9ab,
        0x5be0cd19
    ]]
    
    W = []

    for i in range(1, N + 1):
        a,b,c,d,e,f,g,h = H[i-1]

        for j in range(64):
            
            if(j < 16):
                W.append(M[i - 1][j])
            else:
                W.append(add(
                    lcSigma1(W[j - 2]),
                             W[j - 7],
                    lcSigma0(W[j - 15]),
                             W[j - 16]
                ))
            
            T1 = add(
                h,
                ucSigma1(e),
                Ch(e,f,g),
                K[j],
                W[j]
            )
            T2 = add(ucSigma0(a), Maj(a, b, c))

            h = g
            g = f
            f = e 
            e = add(d, T1)
            d = c
            c = b
            b = a
            a = add(T1, T2)

        H.append([
            add(a, H[i-1][0]),
            add(b, H[i-1][1]),
            add(c, H[i-1][2]),
            add(d, H[i-1][3]),
            add(e, H[i-1][4]),
            add(f, H[i-1][5]),
            add(g, H[i-1][6]),
            add(h, H[i-1][7]),
        ])

    hsh = ''
    for n in H[-1]:
        hsh += hex(n)[2:]
    return hsh

def padMessage(msg):
    newS = toBinString(msg) + '1'
    l = len(newS) - 1
    k = 0

    while((l + 1 + k) % 512 != 448):
        newS += '0'
        k += 1
    
    binL = bin(l)[2:]
    newS += '0' * (64 - len(binL)) + binL
    return newS

def parseMessage(msg):
    M = []
    N = len(msg) // 512
    for i in range(N):
        M.append([])
        Mi = msg[i * 512: (i + 1) * 512]
        for j in range(0, 512, 32):
            M[i].append(int(Mi[j: j + 32], 2))
    return M

def add(*args):
    sum_ = 0
    for arg in args:
        sum_ += arg
    return sum_ & ((1 << 32) - 1)

#Logical functions used by the SHA 256 algorithm
############################################################################################################
#Choose function
def Ch(x, y, z):
    """
    For each bit index, that result bit is according to the bit from y (or respectively z ) at this index,
    depending on if the bit from x at this index is 1 (or respectively 0).
    """
    return (x & y) ^ (bitnegate(x) & z)

#Majority function
def Maj(x, y, z):
    return(x & y) ^ (x & z) ^ (y & z)

#Σ0 function 
def ucSigma0(x):
    return rRot(x, 2) ^ rRot(x, 13) ^ rRot(x, 22)

#Σ1 function
def ucSigma1(x):
    return rRot(x, 6) ^ rRot(x, 11) ^ rRot(x, 25)

#σ0 function
def lcSigma0(x):
    return rRot(x, 7) ^ rRot(x, 18) ^ (x >> 3)

#σ1 function
def lcSigma1(x):
    return rRot(x, 17) ^ rRot(x, 19) ^ (x >> 10)
############################################################################################################

#Right rotate a 32-bit unsigned integer
def rRot(x, n):
    remainder = x & ((1 << n) - 1)
    remainder <<= 32 - n
    x = (x >> n) | remainder
    return x

#Bitwise negation of a 32-bit unsigned integer
def bitnegate(x):
    x = '0' * (32 - int.bit_length(x)) + bin(x)[2:]
    s = ''
    bitInvert = lambda b : '1' if b == '0' else '0'
    for b in x:
        s += bitInvert(b)
    return int(s, 2)

#Converts a string to its appropiate binary string
def toBinString(M):
    s = ''
    for c in M:
        charBin = bin(ord(c))[2:]
        charBin = '0' * (8 - len(charBin)) + charBin
        s += charBin
    return s
