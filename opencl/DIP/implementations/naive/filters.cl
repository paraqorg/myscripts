__kernel void rgbnaargrey(                                                 
   __global unsigned char * inp,                                                  
   __global unsigned char * out,                                                  
   const unsigned int width,
   const unsigned int height
)                                           
{                                                                      
   int i = get_global_id(0); 
   __private float r,g,b;                                          
   
	if(i < width*height)                                                       
    { 

		r = convert_float( inp[i] );
		g = convert_float( inp[(width * height)+i] );
		b = convert_float( inp[(2*width * height)+i] );
		//g=	

  	 	out[i]=convert_char((0.3f *r) + (0.59f * g)+(0.11f *b));
    }   
 //c[i] = a[i] + b[i];                                             
}
