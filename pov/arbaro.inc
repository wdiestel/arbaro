
#declare Arb_leaf_disc = disc { <0,0,0>,<0,0,-1>,0.5 } 

#declare Arb_leaf_disc_rot90 = disc { <0,0,0>,<1,0,0>,0.5 } 

/*
#declare Arb_leaf_half = difference {
   cylinder { <0, 0, -1e-05>, <0, 0, 1e-05>, 0.5 }
   box { <-0.5, -0.5, -0.5>, <0.5, 0.5, 0.5> translate x*0.5 }
}

#declare Arb_leaf_palm = union {
   object { Arb_leaf_half rotate y*(-20) }
   object { Arb_leaf_half rotate y*(-160) }
}
*/

#declare Arb_leaf_palm = intersection {
   box { <-0.5, -0.5, 1e-03>, <0.5, 0.5, -0.2> }
   union { cylinder { <0, 0, -1e-04>, <0, 0, 1e-04>, 0.5 rotate y*(-20) }
           cylinder { <0, 0, -1e-04>, <0, 0, 1e-04>, 0.5 rotate y*(-160) }
   }
}

#declare Arb_leaf_sphere = sphere { <0,0,0>,0.5} 

#declare Arb_leaf_square = box  { <-0.5, -0.5, 1e-03>, <0.5, 0.5, -0.001> }


