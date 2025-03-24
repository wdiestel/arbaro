surface leaf2() {

	float r = 0.5;
	float x = s - r;
	float y = t - r;
	float where = y * y + x * x;

	Oi = Os * ( 1 - filterstep( r * r, where ) );
	Ci = Oi;
}
