__attribute__((reqd_work_group_size(128, 1, 1)))
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


#pragma OPENCL EXTENSION cl_amd_printf : enable
__attribute__((reqd_work_group_size(128, 1, 1)))
__kernel void histgpu(                                                 
   __global unsigned char * inp,                                                  
   __global unsigned int * out,                                                  
   const unsigned int width,
   const unsigned int height
)                                           
{                                                                      
   
	int i = get_global_id(0);
                                             
//__private unsigned int value;
	if(i < width*height)                                                       
    { 
		//value= convert_int(inp[i]);
		//printf("value=%d\n",value);
		out[convert_int(inp[i])] += 1;
		//printf("out[value]=%d",out[value]);
		
   }   
 //c[i] = a[i] + b[i];                                             
}

__attribute__((reqd_work_group_size(128, 1, 1)))
__kernel void congpu(                                                 
   __global unsigned char * grayImage,                                                  
   //__global unsigned int * out,                                                  
   const unsigned int width,
   const unsigned int height,
   const unsigned int d_min,
   const unsigned int d_max
)                                           
{                                                                      
   
	int i = get_global_id(0);
        unsigned char pixel;                            
//__private unsigned int value;
	if(i < width*height)                                                       
    { 
		pixel = grayImage[i];

			if ( pixel < d_min ) 
			{
				pixel = 0;
			}
			else if ( pixel > d_max ) 
			{
				pixel = 255;
			}
			else 
			{
				pixel = convert_char(255.0f * (pixel - d_min) / (d_max-d_min));
			}
			
			grayImage[i] = pixel;
		
   }   
 //c[i] = a[i] + b[i];                                             
}

