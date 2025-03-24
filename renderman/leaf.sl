/**
 * Poor man's leaf shader.
 * Renders rectangular geometry with default parameterization (0..1) as
 * ellipses via opacity modulation.
 *
 */
surface leaf() {
	normal NN = normalize( faceforward( N, I, N ) );

	float r = 0.5;
	float x = s - r;
	float y = t - r;
	float where = y * y + x * x;

	Oi = Os * ( 1 - filterstep( r * r, where ) );
	Ci = Oi * Cs * ( 0.65 * diffuse( NN ) + 0.15 * diffuse( -NN ) + 0.15 );
}
