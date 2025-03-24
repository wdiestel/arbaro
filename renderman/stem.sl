/**
 * Poor man's stem shader.
 * Renders curves geometry as tubes via bump mapping.
 *
 */
surface stem(
	varying float width = 0;
) {
	normal NN = normalize( N );

	// We're test if the stem is made from a curve gprim.
	// If so, we do bump mapping to fake the appearance
	// of a cylinder.
	uniform float flag; // Apodaca device
	if( width ) flag = 1; else flag = 0;
	if( flag ) {
		float r = 0.5 * width;
		float x = s - r;
		float y = sqrt( r * r - x * x );

		float spacescale = length( transform( "object", NN ) );
		vector Ndisp = NN * ( r / max( spacescale, 1e-6 ) );
		point PP = P + Ndisp;
		NN = normalize( calculatenormal( PP ) );
	}

	Oi = Os;
	Ci = Oi * Cs * ( 0.8 * diffuse( NN ) + 0.15 );
}
