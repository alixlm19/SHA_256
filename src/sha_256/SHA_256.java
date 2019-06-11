package sha_256;

import java.math.BigInteger;

public class SHA_256 {
	private static int[] words = {0x428a2f98,0x71374491,0xb5c0fbcf,0xe9b5dba5,0x3956c25b,0x59f111f1,0x923f82a4,0xab1c5ed5,
							  0xd807aa98,0x12835b01,0x243185be,0x550c7dc3,0x72be5d74,0x80deb1fe,0x9bdc06a7,0xc19bf174,
							  0xe49b69c1,0xefbe4786,0x0fc19dc6,0x240ca1cc,0x2de92c6f,0x4a7484aa,0x5cb0a9dc,0x76f988da,
							  0x983e5152,0xa831c66d,0xb00327c8,0xbf597fc7,0xc6e00bf3,0xd5a79147,0x06ca6351,0x14292967,
							  0x27b70a85,0x2e1b2138,0x4d2c6dfc,0x53380d13,0x650a7354,0x766a0abb,0x81c2c92e,0x92722c85,
							  0xa2bfe8a1,0xa81a664b,0xc24b8b70,0xc76c51a3,0xd192e819,0xd6990624,0xf40e3585,0x106aa070,
							  0x19a4c116,0x1e376c08,0x2748774c,0x34b0bcb5,0x391c0cb3,0x4ed8aa4a,0x5b9cca4f,0x682e6ff3,
							  0x748f82ee,0x78a5636f,0x84c87814,0x8cc70208,0x90befffa,0xa4506ceb,0xbef9a3f7,0xc67178f2};

	
	public BigInteger digest(String message) {
		String bin = strToBin(message);
		//Checks if the binary string size is a multiple of 512
		if(bin.length() % 512 != 0) {
			bin = bin + '1';
			int k = 0;
			while((bin.length() + 1 + k) % 512 != 448 % 512) {
				bin = bin + '0';
				k++;
			}
		}
		
		System.out.println(bin);
		BigInteger msg = new BigInteger("1");

		return msg;
	}
	
	private String strToBin(String message) {
		byte[] msgBytes = message.getBytes();
		String binary = "";
		int val;
		for(byte b : msgBytes) {
			val = b;
			for(int i = 0; i < 8; i++) {
				binary = binary + Integer.toString((val & 128) == 0 ? 0 : 1);
				val <<= 1;
			}
		}
		return binary;
	}
	
	public int RotR(int A, int n) {
		/**Circular right shift of n bits of the binary word A**/
		int lsb;
		for(int i = 0; i < n; i++) {
			lsb = (int) A & 0b1;			//Returns the LSB of A
			A >>= 1; 						//Shift A 1 bit to the right
			A -= 0x80000000;				//Removes the MSB result of the bit shifting operation
			A |= lsb << 31;					//Replaces the MSB with the LSB
		}
		return A;
	}
	
	public int Ch(int X, int Y, int Z) {
		return (X & Y) ^ (~X & Z);
	}
	
	public int Maj(int X, int Y, int Z) {
		return (X & Y) ^ (X & Z) ^ (Y & Z);
	}
	
	public int sum_0(int X) {
		return RotR(X, 2) ^ RotR(X, 13) ^ RotR(X, 22);
	}
	
	public int sum_1(int X) {
		return RotR(X, 6) ^ RotR(X, 11) ^ RotR(X, 25);
	}
	
	public int sigma_0(int X) {
		return RotR(X, 7) ^ RotR(X, 18) ^ RotR(X, 3);
	}

	public int sigma_1(int X) {
		return RotR(X, 17) ^ RotR(X, 19) ^ RotR(X, 10);
	}
}
