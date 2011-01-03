package Helpers;

import java.util.Random;

/**
 * Noise function from Ken Perlin.
 * 
 * @link http://mrl.nyu.edu/~perlin/noise/
 */
public final class Perlin {

    static boolean matrixHasBeenRegenerated;
    public double persistence; // persistence
    public int n ; // #octaves 

////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////// Shape Methods  //////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////

	

	public    double stripes(double x, double f) {

		double t = .5 + .5 * Math.sin(f * 2*Math.PI * x);
		return t * t - .5;
	}


	public    double turbulence(double x, double y, double z,int freq) {
		double t = -.5;
		for (float f = 1 ; f <=  freq ; f *= 2)
			t += Math.abs(snoise((float)(f*x),(float)(f*y),(float)(f*z)) / f);
		return t; 
	}

	public    double turbulence(double x, double y,int freq) {
		double t = -.5;

		for (float f = 1 ; f <=  freq ; f *= 2)
			t += Math.abs(snoise((float)(f*x),(float)(f*y)) / f);
		return t; 
	}
	
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////// Shape Tweak ToolKit  ////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////


	public    double bias(double a, double  b)
	{
		return Math.pow(a, Math.log(b) / Math.log(0.5));
	}

	public    double gain(double a, double b)
	{
		double p = Math.log(1. - b) / Math.log(0.5);

		if (a < .001)
			return 0.;
		else if (a > .999)
			return 1.;
		if (a < 0.5)
			return Math.pow(2 * a, p) / 2;
		else
			return 1. - Math.pow(2 * (1. - a), p) / 2;
	}

/*  The above two functions provide a surprisingly complete set of tools for tweak- ing things.
 *  If you want to make something look “darker” or “more transparent,” you would generally use 
 *  biasb with b < 0.5. Conversely, if you want to make some- thing look “brighter” or
 *  “more opaque,” you would generally use biasb with b > 0.5. Similarly, if you want to “fuzz out”
 *   a texture, you would generally use gaing with g < 0.5. Conversely, if you want to “sharpen” a 
 *   texture, you would generally use gaing with g > 0.5. Most of the time, you’ll want to instrument 
 *   various numeri- cal parameters with bias and gain settings and tune things to taste.
 */


////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////// Configurable Noise with octaves  //////////////////////////////////////// 
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////


	public Perlin(){

            if(!matrixHasBeenRegenerated){
		regenerateMatrix();
                matrixHasBeenRegenerated = true;
            }
		persistence = 1;
		n = 4;
	}
	
	public    final double perlinNoise(double x,double y, double z){

		double total = 0;
		

		double frequency, amplitude;

		for(int i = 0; i < n ; i++){

			frequency = Math.pow(2,i);
			amplitude = Math.pow(persistence, i);

			total +=  snoise((float)(x*frequency), (float)(y*frequency), (float)(z*frequency)) * amplitude;

		}


		return total;

	}
	
	public    final double perlinNoise(double x,double y){

		double total = 0;
		

		double frequency, amplitude;

		for(int i = 0; i < n ; i++){

			frequency = Math.pow(2,i);
			amplitude = Math.pow(persistence, i);

			total +=  snoise((float)(x*frequency), (float)(y*frequency),(float)(y*frequency)) * amplitude;

		}


		return total;

	}




////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////Perlin Noise Implementation ////////////////////////////////////////////// 
////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static   final float[] G1 = { -1, 1 };
	private static  final float[][] G2 = { { 1, 0 }, { -1, 0 }, { 0, 1 },
		{ 0, -1 } };
	private  static   final float[][] G3 = { { 1, 1, 0 }, { -1, 1, 0 },
		{ 1, -1, 0 }, { -1, -1, 0 }, { 1, 0, 1 }, { -1, 0, 1 },
		{ 1, 0, -1 }, { -1, 0, -1 }, { 0, 1, 1 }, { 0, -1, 1 },
		{ 0, 1, -1 }, { 0, -1, -1 }, { 1, 1, 0 }, { -1, 1, 0 },
		{ 0, -1, 1 }, { 0, -1, -1 } };
	private  static final float[][] G4 = { { -1, -1, -1, 0 }, { -1, -1, 1, 0 },
		{ -1, 1, -1, 0 }, { -1, 1, 1, 0 }, { 1, -1, -1, 0 },
		{ 1, -1, 1, 0 }, { 1, 1, -1, 0 }, { 1, 1, 1, 0 },
		{ -1, -1, 0, -1 }, { -1, 1, 0, -1 }, { 1, -1, 0, -1 },
		{ 1, 1, 0, -1 }, { -1, -1, 0, 1 }, { -1, 1, 0, 1 },
		{ 1, -1, 0, 1 }, { 1, 1, 0, 1 }, { -1, 0, -1, -1 },
		{ 1, 0, -1, -1 }, { -1, 0, -1, 1 }, { 1, 0, -1, 1 },
		{ -1, 0, 1, -1 }, { 1, 0, 1, -1 }, { -1, 0, 1, 1 }, { 1, 0, 1, 1 },
		{ 0, -1, -1, -1 }, { 0, -1, -1, 1 }, { 0, -1, 1, -1 },
		{ 0, -1, 1, 1 }, { 0, 1, -1, -1 }, { 0, 1, -1, 1 },
		{ 0, 1, 1, -1 }, { 0, 1, 1, 1 } };
	private  int[] p = { 151, 160, 137, 91, 90, 15, 131, 13, 201,
		95, 96, 53, 194, 233, 7, 225, 140, 36, 103, 30, 69, 142, 8, 99, 37,
		240, 21, 10, 23, 190, 6, 148, 247, 120, 234, 75, 0, 26, 197, 62,
		94, 252, 219, 203, 117, 35, 11, 32, 57, 177, 33, 88, 237, 149, 56,
		87, 174, 20, 125, 136, 171, 168, 68, 175, 74, 165, 71, 134, 139,
		48, 27, 166, 77, 146, 158, 231, 83, 111, 229, 122, 60, 211, 133,
		230, 220, 105, 92, 41, 55, 46, 245, 40, 244, 102, 143, 54, 65, 25,
		63, 161, 1, 216, 80, 73, 209, 76, 132, 187, 208, 89, 18, 169, 200,
		196, 135, 130, 116, 188, 159, 86, 164, 100, 109, 198, 173, 186, 3,
		64, 52, 217, 226, 250, 124, 123, 5, 202, 38, 147, 118, 126, 255,
		82, 85, 212, 207, 206, 59, 227, 47, 16, 58, 17, 182, 189, 28, 42,
		223, 183, 170, 213, 119, 248, 152, 2, 44, 154, 163, 70, 221, 153,
		101, 155, 167, 43, 172, 9, 129, 22, 39, 253, 19, 98, 108, 110, 79,
		113, 224, 232, 178, 185, 112, 104, 218, 246, 97, 228, 251, 34, 242,
		193, 238, 210, 144, 12, 191, 179, 162, 241, 81, 51, 145, 235, 249,
		14, 239, 107, 49, 192, 214, 31, 181, 199, 106, 157, 184, 84, 204,
		176, 115, 121, 50, 45, 127, 4, 150, 254, 138, 236, 205, 93, 222,
		114, 67, 29, 24, 72, 243, 141, 128, 195, 78, 66, 215, 61, 156, 180,
		151, 160, 137, 91, 90, 15, 131, 13, 201, 95, 96, 53, 194, 233, 7,
		225, 140, 36, 103, 30, 69, 142, 8, 99, 37, 240, 21, 10, 23, 190, 6,
		148, 247, 120, 234, 75, 0, 26, 197, 62, 94, 252, 219, 203, 117, 35,
		11, 32, 57, 177, 33, 88, 237, 149, 56, 87, 174, 20, 125, 136, 171,
		168, 68, 175, 74, 165, 71, 134, 139, 48, 27, 166, 77, 146, 158,
		231, 83, 111, 229, 122, 60, 211, 133, 230, 220, 105, 92, 41, 55,
		46, 245, 40, 244, 102, 143, 54, 65, 25, 63, 161, 1, 216, 80, 73,
		209, 76, 132, 187, 208, 89, 18, 169, 200, 196, 135, 130, 116, 188,
		159, 86, 164, 100, 109, 198, 173, 186, 3, 64, 52, 217, 226, 250,
		124, 123, 5, 202, 38, 147, 118, 126, 255, 82, 85, 212, 207, 206,
		59, 227, 47, 16, 58, 17, 182, 189, 28, 42, 223, 183, 170, 213, 119,
		248, 152, 2, 44, 154, 163, 70, 221, 153, 101, 155, 167, 43, 172, 9,
		129, 22, 39, 253, 19, 98, 108, 110, 79, 113, 224, 232, 178, 185,
		112, 104, 218, 246, 97, 228, 251, 34, 242, 193, 238, 210, 144, 12,
		191, 179, 162, 241, 81, 51, 145, 235, 249, 14, 239, 107, 49, 192,
		214, 31, 181, 199, 106, 157, 184, 84, 204, 176, 115, 121, 50, 45,
		127, 4, 150, 254, 138, 236, 205, 93, 222, 114, 67, 29, 24, 72, 243,
		141, 128, 195, 78, 66, 215, 61, 156, 180 };




	public    final float snoise(float x) {
		int xf = (int) Math.floor(x);
		int X = xf & 255;
		x -= xf;
		float u = fade(x);
		int A = p[X], B = p[X + 1];
		return lerp(u, grad(p[A], x), grad(p[B], x - 1));
	}

	public    final float snoise(float x, float y) {
		int xf = (int) Math.floor(x);
		int yf = (int) Math.floor(y);
		int X = xf & 255;
		int Y = yf & 255;
		x -= xf;
		y -= yf;
		float u = fade(x);
		float v = fade(y);
		int A = p[X] + Y, B = p[X + 1] + Y;
		return lerp(v, lerp(u, grad(p[A], x, y), grad(p[B], x - 1, y)), lerp(u, grad(p[A + 1], x, y - 1), grad(p[B + 1], x - 1, y - 1)));
	}

	public    final float snoise(float x, float y, float z) {
		int xf = (int) Math.floor(x);
		int yf = (int) Math.floor(y);
		int zf = (int) Math.floor(z);
		int X = xf & 255;
		int Y = yf & 255;
		int Z = zf & 255;
		x -= xf;
		y -= yf;
		z -= zf;
		float u = fade(x);
		float v = fade(y);
		float w = fade(z);
		int A = p[X] + Y, AA = p[A] + Z, AB = p[A + 1] + Z, B = p[X + 1] + Y, BA = p[B] + Z, BB = p[B + 1] + Z;
		return lerp(w, lerp(v, lerp(u, grad(p[AA], x, y, z), grad(p[BA], x - 1, y, z)), lerp(u, grad(p[AB], x, y - 1, z), grad(p[BB], x - 1, y - 1, z))), lerp(v, lerp(u, grad(p[AA + 1], x, y, z - 1), grad(p[BA + 1], x - 1, y, z - 1)), lerp(u, grad(p[AB + 1], x, y - 1, z - 1), grad(p[BB + 1], x - 1, y - 1, z - 1))));
	}

	public    final float snoise(float x, float y, float z, float w) {
		int xf = (int) Math.floor(x);
		int yf = (int) Math.floor(y);
		int zf = (int) Math.floor(z);
		int wf = (int) Math.floor(w);
		int X = xf & 255;
		int Y = yf & 255;
		int Z = zf & 255;
		int W = wf & 255;
		x -= xf;
		y -= yf;
		z -= zf;
		w -= wf;
		float u = fade(x);
		float v = fade(y);
		float t = fade(z);
		float s = fade(w);
		int A = p[X] + Y, AA = p[A] + Z, AB = p[A + 1] + Z, B = p[X + 1] + Y, BA = p[B] + Z, BB = p[B + 1] + Z, AAA = p[AA] + W, AAB = p[AA + 1] + W, ABA = p[AB] + W, ABB = p[AB + 1] + W, BAA = p[BA] + W, BAB = p[BA + 1] + W, BBA = p[BB] + W, BBB = p[BB + 1] + W;
		return lerp(s, lerp(t, lerp(v, lerp(u, grad(p[AAA], x, y, z, w), grad(p[BAA], x - 1, y, z, w)), lerp(u, grad(p[ABA], x, y - 1, z, w), grad(p[BBA], x - 1, y - 1, z, w))), lerp(v, lerp(u, grad(p[AAB], x, y, z - 1, w), grad(p[BAB], x - 1, y, z - 1, w)), lerp(u, grad(p[ABB], x, y - 1, z - 1, w), grad(p[BBB], x - 1, y - 1, z - 1, w)))), lerp(t, lerp(v, lerp(u, grad(p[AAA + 1], x, y, z, w - 1), grad(p[BAA + 1], x - 1, y, z, w - 1)), lerp(u, grad(p[ABA + 1], x, y - 1, z, w - 1), grad(p[BBA + 1], x - 1, y - 1, z, w - 1))), lerp(v, lerp(u, grad(p[AAB + 1], x, y, z - 1, w - 1), grad(p[BAB + 1], x - 1, y, z - 1, w - 1)), lerp(u, grad(p[ABB + 1], x, y - 1, z - 1, w - 1), grad(p[BBB + 1], x - 1, y - 1, z - 1, w - 1)))));
	}

	private    final float fade(float t) {
		return t * t * t * (t * (t * 6 - 15) + 10);
	}

	private    final float lerp(float t, float a, float b) {
		return a + t * (b - a);
	}

	private    final float grad(int hash, float x) {
		int h = hash & 0x1;
		return x * G1[h];
	}

	private    final float grad(int hash, float x, float y) {
		int h = hash & 0x3;
		return x * G2[h][0] + y * G2[h][1];
	}

	private    final float grad(int hash, float x, float y, float z) {
		int h = hash & 15;
		return x * G3[h][0] + y * G3[h][1] + z * G3[h][2];
	}

	private    final float grad(int hash, float x, float y, float z, float w) {
		int h = hash & 31;
		return x * G4[h][0] + y * G4[h][1] + z * G4[h][2] + w * G4[h][3];
	}


	public    final int B =  256;
	

	public    void regenerateMatrix()
	{
		int i, j, k;
		Random r  = new Random(System.currentTimeMillis());

		for (i = 0 ; i < B ; i++) {
			p[i] = Math.abs((r.nextInt() % B));
		}

		while ((--i == 0)) {
			k = p[i];
			p[i] = p[j = r.nextInt()%B];
			p[j] = k;
		}

		for (i = 0 ; i < B  ; i++) {
			p[B + i] = p[i];
		}
	}


}