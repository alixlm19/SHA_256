package sha_256;

import java.math.BigInteger;
import java.util.ArrayList;

public class SHA_256 {
	private static final int[] K = {0x428a2f98,0x71374491,0xb5c0fbcf,0xe9b5dba5,0x3956c25b,0x59f111f1,0x923f82a4,0xab1c5ed5,
							  0xd807aa98,0x12835b01,0x243185be,0x550c7dc3,0x72be5d74,0x80deb1fe,0x9bdc06a7,0xc19bf174,
							  0xe49b69c1,0xefbe4786,0x0fc19dc6,0x240ca1cc,0x2de92c6f,0x4a7484aa,0x5cb0a9dc,0x76f988da,
							  0x983e5152,0xa831c66d,0xb00327c8,0xbf597fc7,0xc6e00bf3,0xd5a79147,0x06ca6351,0x14292967,
							  0x27b70a85,0x2e1b2138,0x4d2c6dfc,0x53380d13,0x650a7354,0x766a0abb,0x81c2c92e,0x92722c85,
							  0xa2bfe8a1,0xa81a664b,0xc24b8b70,0xc76c51a3,0xd192e819,0xd6990624,0xf40e3585,0x106aa070,
							  0x19a4c116,0x1e376c08,0x2748774c,0x34b0bcb5,0x391c0cb3,0x4ed8aa4a,0x5b9cca4f,0x682e6ff3,
							  0x748f82ee,0x78a5636f,0x84c87814,0x8cc70208,0x90befffa,0xa4506ceb,0xbef9a3f7,0xc67178f2};

	
	private ArrayList<String> M;
	private String[][] blocks;
	
	public String digest(String message) {
		M = new ArrayList<String>();
		String msg = strToBin(message);
			
		//Check if the binary string size is a multiple of 512
		int l = msg.length();
		msg += '1';
		int k = 0;
		
		//Appends K bits to the binString until l + 1 + k is congruent to 448 mod 512
		while((l + 1 + k) % 512 != 448 % 512) {
			msg += "0";
			k++;
		}
		
		//Represent the length l < 2^64 as a 64 bit number
		String lBinString = Long.toBinaryString(l);
		int lBinStringLength = lBinString.length();
		for(int i = 0; i < 64 - lBinStringLength; i++) {
			lBinString = "0" + lBinString;
		}
		msg += lBinString;					//We end up with a 512 bit binString

		//Split the message into blocks of 512 bits
		int start = 0;
		int end = 0;
		
		while(end != msg.length()) {
			if(end + 512 < msg.length())
				end += 512;
			else {
				end = msg.length();
			}
			M.add(msg.substring(start, end));
			start = end;
		}
		
		
		System.out.println("msg: " + msg);
		for(int i = 0; i < M.size(); i++) {
			System.out.println("M[" + i + "]" + " " +  M.get(i));
		}
		System.out.println();
		
		
		//Decompose to binString
		decompose();
		
		
		System.out.println(computeHash());
		BigInteger hash = new BigInteger(computeHash(), 2);
		
		return hash.toString();
	}
	
	private void decompose() {
		blocks = new String[M.size()][];
		String[] W;
		int blockIndex = 0;
		
		for(String s : M) {
			W = new String[64];
			//Gets the first 16 blocks by splitting M in 32-bit b
			for(int i = 0; i < 16; i++) {
				W[i] = s.substring(i * 32, (i + 1) * 32);
				//System.out.println(W[i] + " " +  W[i].length());
			}
			
			//the remaining 48 are obtained with the formula:
			for(int i = 16; i < 64; i++) {
				String a = sigma_1(W[i-2]);
				String b = W[i-7];
				String c = sigma_0(W[i-15]);
				String d = W[i-16];
				
				W[i] = addMod(a, b, c, d);
				//System.out.println(W[i].length() + " " + W[i]);
			}
			
			blocks[blockIndex] = W.clone();
			blockIndex++;
		}
		
	}
	
	private String computeHash() {
//		int[] HLong = {0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a,
//				0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19};
//		
//		String[] H = new String[8];
//		for(int i = 0; i < 8; i++) {
//			H[i] = Integer.toBinaryString(HLong[i]);
//			String padding = "";
//			for(int j = 0; j < 32 - H[i].length(); j++) {
//				padding += "0";
//			}
//			H[i] = padding + H[i];
//		}
		String[][] H = new String[M.size()][8];
		H[0][0] = "01101010000010011110011001100111";
		H[0][1] = "10111011011001111010111010000101";
		H[0][2] = "00111100011011101111001101110010";
		H[0][3] = "10100101010011111111010100111010";
		H[0][4] = "01010001000011100101001001111111";
		H[0][5] = "10011011000001010110100010001100";
		H[0][6] = "00011111100000111101100110101011";
		H[0][7] = "01011011111000001100110100011001";
		
		
		String a = H[0][0];
		String b = H[0][1];
		String c = H[0][2];
		String d = H[0][3];
		String e = H[0][4];
		String f = H[0][5];
		String g = H[0][6];
		String h = H[0][7];

		
		//Iterate over each block of M
		for(int t = 1; t < M.size(); t++){
			for(int i = 0; i < 64; i++) {
			String Ki = Integer.toBinaryString(K[i]);
			
			//Format Ki
			String padding = "";
			for(int j = 0; j < 32 - Ki.length(); j++) {
				padding += "0";
			}
			Ki = padding + Ki;
			
			
			String T1 = addMod(h, sum_1(e), Ch(e, f, g), Ki, blocks[t-1][i]);
			String T2 = addMod(sigma_0(a), Maj(a, b, c));
			h = g;
			g = f;
			f = e;
			e = addMod(d, T1);
			d = c;
			c = b;
			b = a;
			a = addMod(T1, T2);
			
			
			H[t][0] = addMod(H[t-1][0], a);
			H[t][1] = addMod(H[t-1][1], b);
			H[t][2] = addMod(H[t-1][2], c);
			H[t][3] = addMod(H[t-1][3], d);
			H[t][4] = addMod(H[t-1][4], e);
			H[t][5] = addMod(H[t-1][5], f);
			H[t][6] = addMod(H[t-1][6], g);
			H[t][7] = addMod(H[t-1][7], h);
			
			}
		}
		
		String hash = "";
		for(String s : H[M.size()-1]) {
			hash += s;
		}
		return hash;
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
		System.out.println("bin: " + binary.length());
		return binary;
	}
	
	private String addMod(String ...args){
		long sum = 0;
		long c = 4294967296l;
		for(String s : args) {
			sum += Long.parseLong(s, 2) % c;
		}
		String result = Long.toBinaryString(sum % c);
		String padding = "";
		for(int i = 0; i < 32 - result.length(); i++) {
			padding += "0";
		}
		result = padding + result;
		return result;
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
