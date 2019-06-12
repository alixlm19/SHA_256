package sha_256;

import java.math.BigInteger;

public class SHA_256 {
	private static int[] K = {0x428a2f98,0x71374491,0xb5c0fbcf,0xe9b5dba5,0x3956c25b,0x59f111f1,0x923f82a4,0xab1c5ed5,
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
		System.out.println("bin: " + bin);
		int l = bin.length();
		bin += '1';
		int k = 0;
		
		//Appends K bits to the binString until l + 1 + k is congruent to 448 mod 512
		while((l + 1 + k) % 512 != 448 % 512) {
			bin += "0";
			k++;
		}
		
		//Represent the length l < 2^64 as a 64 bit number
		String lBinString = Long.toBinaryString(l);
		int lBinStringLength = lBinString.length();
		for(int i = 0; i < 64 - lBinStringLength; i++) {
			lBinString = "0" + lBinString;
		}
		bin += lBinString;					//We end up with a 512 bit binString
		
		//Decompose to binString
		decompose(bin);
		
		BigInteger msg = new BigInteger("1");

		return msg;
	}
	
	private String decompose(String M) {
		String[] W = new String[64];
		System.out.println(M.length());
		
		//Gets the first 16 blocks by splitting M in 32-bit b
		for(int i = 0; i < 16; i++) {
			W[i] = M.substring(i * 32, (i + 1) * 32);
			//System.out.println(W[i] + " " +  W[i].length());
		}
		
		//the remaining 48 are obtained with the formula:
		for(int i = 16; i < 64; i++) {
			String a = sigma_1(W[i - 2]);
			//System.out.println(W[i].length());
		}
		
		return "";
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
	
	public String RotR(String A, int n) {
		/**Circular right shift of n bits of the binary word A**/
		for(int i = 0; i < n; i++) {
			A = A.charAt(A.length() - 1) + A.substring(0, A.length() - 1);
		}
		return A;
	}
	
	private String AND(String A, String B) {
		String result = "";
		for(int i = 0; i < A.length(); i++) {
			result += (A.charAt(i) == '1' && B.charAt(i) == '1') ? "1" : "0";
		}
		return result;
	}
	
	private String XOR(String ...args) {
		String result = args[0];
		if(args.length > 1) {
			String temp;
			for(int i = 1; i < args.length; i++) {
				temp = "";
				for(int j = 0; j < args[i].length(); j++) {
					temp += (result.charAt(j) == args[i].charAt(j))? "0" : "1";
				}
				result = temp;
			}
		}
		//System.out.println("asdf" + result.length());
		return result;
	}
	
	private String OR() {
		return "";
	}
	
	public String complement(String A) {
		String result = "";
		for(char c : A.toCharArray()) {
			result += c == '1' ?  "0" : "1";
		}
		return result;
	}
	
	public String Ch(String X, String Y, String Z) {
		return XOR(AND(X, Y), AND(complement(X), Z));
	}
	
	public String Maj(String X, String Y, String Z) {
		return XOR(AND(X, Y), AND(X, Z), AND(Y, Z));
	}
	
	public String sum_0(String X) {
		return XOR(RotR(X, 2), RotR(X, 13), RotR(X, 22));
	}
	
	public String sum_1(String X) {
		return XOR(RotR(X, 6), RotR(X, 11), RotR(X, 25));
	}
	
	public String sigma_0(String X) {
		return XOR(RotR(X, 7), RotR(X, 18), RotR(X, 3));
	}

	public String sigma_1(String X) {
		return XOR(RotR(X, 17), RotR(X, 19), RotR(X, 10));
	}
}
