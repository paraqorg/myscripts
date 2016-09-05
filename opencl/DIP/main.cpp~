#include "CImg.h"
#include "Timer.hpp"
#include <iostream>
#include <iomanip>
#include <cstring>
//#include <OpenCL/opencl.h>
#include <CL/cl.h>
#include "err_code.h"


#ifndef DEVICE
#define DEVICE CL_DEVICE_TYPE_DEFAULT
#endif
#define MAX_SOURCE_SIZE (0x100000)

using cimg_library::CImg;
using LOFAR::NSTimer;
using std::cout;
using std::cerr;
using std::endl;
using std::fixed;
using std::setprecision;

// Constants
const bool displayImages = false;
const bool saveAllImages = true;
const unsigned int HISTOGRAM_SIZE = 256;
const unsigned int BAR_WIDTH = 4;
const unsigned int CONTRAST_THRESHOLD = 80;
const float filter[] = {	1.0f, 1.0f, 1.0f, 1.0f, 1.0f, 
						1.0f, 2.0f, 2.0f, 2.0f, 1.0f, 
						1.0f, 2.0f, 3.0f, 2.0f, 1.0f, 
						1.0f, 2.0f, 2.0f, 2.0f, 1.0f, 
						1.0f, 1.0f, 1.0f, 1.0f, 1.0f};
extern int output_device_info(cl_device_id );

extern void rgb2gray(unsigned char *inputImage, unsigned char *grayImage, const int width, const int height);
//extern void rgb2grayCuda

extern void histogram1D(unsigned char *grayImage, unsigned char *histogramImage, const int width, const int height, unsigned int *histogram, const unsigned int HISTOGRAM_SIZE, const unsigned int BAR_WIDTH);
//extern void histogram1DCuda 

extern void contrast1D(unsigned char *grayImage, const int width, const int height, unsigned int *histogram, const unsigned int HISTOGRAM_SIZE, const unsigned int CONTRAST_THRESHOLD);
//extern void contrast1DCuda

extern void triangularSmooth(unsigned char *grayImage, unsigned char *smoothImage, const int width, const int height, const float *filter);
//extern void triangularSmoothCuda


int main(int argc, char *argv[]) 
{

    int          err; 
    cl_device_id     device_id;     // compute device id
    cl_context       context;       // compute context
    cl_command_queue commands;      // compute command queue
    cl_program       program;       // compute program
    cl_kernel        ko_vadd,ko_hist,ko_con;       // compute kernel
    //char *kernel_source = getKernelSource("filters.cl");

//buffers
cl_mem d_img;                     // device memory used for the input  a vector
cl_mem d_gry;
cl_mem d_hist;


FILE *fp;
char fileName[] = "filters.cl";
char *source_str;
size_t source_size;
size_t global;
size_t nthreds;

/* Load the source code containing the kernel*/
fp = fopen(fileName, "r");
if (!fp) {
fprintf(stderr, "Failed to load kernel.\n");
exit(1);
}
source_str = (char*)malloc(MAX_SOURCE_SIZE);
source_size = fread(source_str, 1, MAX_SOURCE_SIZE, fp);
fclose(fp);
int i;
//printf("Start 1  \n");

//===================openCL seetup========================================
// Set up platform and GPU device

    cl_uint numPlatforms;

    // Find number of platforms
    err = clGetPlatformIDs(0, NULL, &numPlatforms);
    checkError(err, "Finding platforms");
    if (numPlatforms == 0)
    {
        printf("Found 0 platforms!\n");
        return EXIT_FAILURE;
    }

    // Get all platforms
    cl_platform_id Platform[numPlatforms];
    err = clGetPlatformIDs(numPlatforms, Platform, NULL);
    checkError(err, "Getting platforms");

    // Secure a GPU
    for (i = 0; i < numPlatforms; i++)
    {
        err = clGetDeviceIDs(Platform[i], DEVICE, 1, &device_id, NULL);
        if (err == CL_SUCCESS)
        {
            break;
        }
    }

    if (device_id == NULL)
        checkError(err, "Finding a device");

    err = output_device_info(device_id);
    checkError(err, "Printing device output");

    // Create a compute context
    context = clCreateContext(0, 1, &device_id, NULL, NULL, &err);
    checkError(err, "Creating context");

    // Create a command queue
    commands = clCreateCommandQueue(context, device_id, CL_QUEUE_PROFILING_ENABLE, &err);
    checkError(err, "Creating command queue");

    // Create the compute program from the source buffer
    //program = clCreateProgramWithSource(context, 1, (const char **) & KernelSource, NULL, &err);
	 program = clCreateProgramWithSource(context, 1, (const char **)&source_str,(const size_t *)&source_size, &err);    
	 checkError(err, "Creating program");

//printf("Start 2  \n");

    // Build the program
    err = clBuildProgram(program, 0, NULL, NULL, NULL, NULL);
    if (err != CL_SUCCESS)
    {
        size_t len;
        char buffer[2048];

        printf("Error: Failed to build program executable!\n%s\n", err_code(err));
        clGetProgramBuildInfo(program, device_id, CL_PROGRAM_BUILD_LOG, sizeof(buffer), buffer, &len);
        printf("%s\n", buffer);
        return EXIT_FAILURE;
    }

    // Create the compute kernel from the program
    ko_vadd = clCreateKernel(program, "rgbnaargrey", &err);
    ko_hist = clCreateKernel(program, "histgpu", &err);
    ko_con = clCreateKernel(program, "congpu", &err);		
    checkError(err, "Creating kernel");
//printf("Start 3  \n");



//===================openCL seetup END========================================





	if ( argc != 2 ) {
		cerr << "Usage: " << argv[0] << " <filename>" << endl;
		return 1;
	}

	// Load the input image
	CImg< unsigned char > inputImage = CImg< unsigned char >(argv[1]);
	if ( displayImages ) {
		inputImage.display("Input Image");
	}
	if ( inputImage.spectrum() != 3 ) {
		cerr << "The input must be a color image." << endl;
		return 1;
	}

	// Convert the input image to grayscale 
	CImg< unsigned char > grayImage = CImg< unsigned char >(inputImage.width(), inputImage.height(), 1, 1);
	
	CImg< unsigned char > histogramImage = CImg< unsigned char >(BAR_WIDTH * HISTOGRAM_SIZE, HISTOGRAM_SIZE, 1, 1);
	unsigned int *histogram = new unsigned int [HISTOGRAM_SIZE];
	memset(reinterpret_cast< void * >(histogram), 0, HISTOGRAM_SIZE * sizeof(unsigned int));

for ( unsigned int i1 = 0; i1 < HISTOGRAM_SIZE; i1++ ) 
	{
		//printf("hist[%d]= %d \n",i,histogram[i]);
		//if ( histogram[i] > max ) 
		histogram[i1] = 0;
						
		//	max = histogram[i];
		
	}
	
//===================openCL buffers========================================
int w=inputImage.width();
int h=inputImage.height();
//d_img=clCreateImage(context,CL_MEM_READ_ONLY,_cl_image_format(CL_RGB ,CL_UNORM_SHORT_555),);
printf("\n Width=%d  Height=%d \n", w,h);

d_img= clCreateBuffer(context,  CL_MEM_READ_ONLY,  sizeof(unsigned char) *w*h*3, NULL, &err);
d_gry= clCreateBuffer(context,  CL_MEM_READ_WRITE,  sizeof(unsigned char) *w*h, NULL, &err);
d_hist= clCreateBuffer(context,  CL_MEM_READ_WRITE,  sizeof(unsigned int) *HISTOGRAM_SIZE, NULL, &err);
//d_hist_img= clCreateBuffer(context,  CL_MEM_WRITE_ONLY,  sizeof(unsigned char) *BAR_WIDTH * HISTOGRAM_SIZE*HISTOGRAM_SIZE, NULL, &err);

//printf("Start 4  \n");
 err = clEnqueueWriteBuffer(commands, d_img, CL_TRUE, 0, sizeof(unsigned char) *w*h*3, inputImage, 0, NULL, NULL);
err = clEnqueueWriteBuffer(commands, d_hist, CL_TRUE, 0, sizeof(unsigned int) *HISTOGRAM_SIZE, histogram, 0, NULL, NULL);

 checkError(err, "Copying h_a to device at d_a");

//printf("Start 4 .1  \n");

   // Set the arguments to our compute kernel
    err  = clSetKernelArg(ko_vadd, 0, sizeof(cl_mem), &d_img);
//printf("Start 4 .2 \n");
    err |= clSetKernelArg(ko_vadd, 1, sizeof(cl_mem), &d_gry);
	
//printf("Start 4 .3 \n");    
err |= clSetKernelArg(ko_vadd, 2, sizeof(unsigned int), &w);
//printf("Start 4 .4 \n");   
err |= clSetKernelArg(ko_vadd, 3, sizeof(unsigned int), &h);
    checkError(err, "Setting kernel arguments");






//===================openCL kernel ========================================
global = (w*h);
size_t global1[]= {w*h,1};


nthreds=128;

NSTimer kernelTime = NSTimer("kernelTime", false, false);
	
	cl_event event0,event1;

    clEnqueueNDRangeKernel(commands, ko_vadd, 1, NULL, &global, &nthreds, 0, NULL, &event0);
    
clWaitForEvents (1, &event0);


cl_ulong time_start, time_end;
double total_time;

clGetEventProfilingInfo(event0, CL_PROFILING_COMMAND_START, sizeof(time_start), &time_start, NULL);
clGetEventProfilingInfo(event0, CL_PROFILING_COMMAND_END, sizeof(time_end), &time_end, NULL);
total_time = time_end - time_start;
printf("\nExecution time rgbtogrey in milliseconds = %0.3f ms\n", (total_time / 1000000.0) );


//checkError(err, "Enqueueing kernel");
//kernelTime.start();
    // Wait for the commands to complete before stopping the timer
    err = clFinish(commands);
    checkError(err, "Waiting for kernel to finish");

	//kernelTime.stop();
	
	//cout << fixed << setprecision(6);
	//cout << "rgb2gray (gpu): \t\t" << kernelTime.getElapsed() << " seconds." << endl;

//printf("Start 6  \n");

 err = clEnqueueReadBuffer( commands, d_gry, CL_TRUE, 0, sizeof(unsigned char) *w*h, grayImage, 0, NULL, NULL );  
    if (err != CL_SUCCESS)
    {
        printf("Error: Failed to read output array!\n%s\n", err_code(err));
        exit(1);
    }

//==========================histogram==================================

err  = clSetKernelArg(ko_hist, 0, sizeof(cl_mem), &d_gry);
err |= clSetKernelArg(ko_hist, 1, sizeof(cl_mem), &d_hist);
//err |= clSetKernelArg(ko_hist, 2, sizeof(cl_mem), &d_hist_img);
err |= clSetKernelArg(ko_hist, 2, sizeof(unsigned int), &w);
err |= clSetKernelArg(ko_hist, 3, sizeof(unsigned int), &h);

 //err = clEnqueueWriteBuffer(commands, d_gry, CL_TRUE, 0, sizeof(unsigned char) *w*h, grayImage, 0, NULL, NULL);

clEnqueueNDRangeKernel(commands, ko_hist, 1, NULL, &global, &nthreds, 0, NULL, &event1);
    
clWaitForEvents (1, &event1);


//cl_ulong time_start, time_end;
//double total_time;
unsigned int max =0;
clGetEventProfilingInfo(event1, CL_PROFILING_COMMAND_START, sizeof(time_start), &time_start, NULL);
clGetEventProfilingInfo(event1, CL_PROFILING_COMMAND_END, sizeof(time_end), &time_end, NULL);
total_time = time_end - time_start;
printf("\nExecution time for histogram in milliseconds = %0.3f ms\n", (total_time / 1000000.0) );


err = clEnqueueReadBuffer( commands, d_hist, CL_TRUE, 0, sizeof(unsigned int) *HISTOGRAM_SIZE, histogram, 0, NULL, NULL );  
    if (err != CL_SUCCESS)
    {
        printf("Error: Failed to read output array!\n%s\n", err_code(err));
        exit(1);
    }




for ( unsigned int i = 0; i < HISTOGRAM_SIZE; i++ ) 
	{
		//printf("hist[%d]= %d \n",i,histogram[i]);
		if ( histogram[i] > max ) 
		{
						
			max = histogram[i];
		}
	}

	for ( int x = 0; x < HISTOGRAM_SIZE * BAR_WIDTH; x += BAR_WIDTH ) 
	{
		unsigned int value = HISTOGRAM_SIZE - ((histogram[x / BAR_WIDTH] * HISTOGRAM_SIZE) / max);

		for ( unsigned int y = 0; y < value; y++ ) 
		{
			for ( unsigned int i = 0; i < BAR_WIDTH; i++ ) 
			{
				histogramImage[(y * HISTOGRAM_SIZE * BAR_WIDTH) + x + i] = 0;
			}
		}
		for ( unsigned int y = value; y < HISTOGRAM_SIZE; y++ ) 
		{
			for ( unsigned int i = 0; i < BAR_WIDTH; i++ ) 
			{
				histogramImage[(y * HISTOGRAM_SIZE * BAR_WIDTH) + x + i] = 255;
			}
		}
	}


//printf("Start 7  \n");

//===================openCL kernel END========================================

	
	//rgb2gray(inputImage.data(), grayImage.data(), inputImage.width(), inputImage.height());
	//rgb2grayCuda

	if ( displayImages ) {
		grayImage.display("Grayscale Image");
	}
	if ( saveAllImages ) {
		grayImage.save("./grayscale.bmp");
	}
	
	// Compute 1D histogram
	

	//histogram1D(grayImage.data(), histogramImage.data(), grayImage.width(), grayImage.height(), histogram, HISTOGRAM_SIZE, BAR_WIDTH);
	//histogram1DCuda

	if ( displayImages ) {
		histogramImage.display("Histogram");
	}
	if ( saveAllImages ) {
		histogramImage.save("./histogram.bmp");
	}

//====================================contrast======================
       i=0;
while ( (i < HISTOGRAM_SIZE) && (histogram[i] < CONTRAST_THRESHOLD) ) 
	{
		i++;
	}
	unsigned int min = i;

	i = HISTOGRAM_SIZE - 1;
	while ( (i > min) && (histogram[i] < CONTRAST_THRESHOLD) ) 
	{
		i--;
	}
	 max = i;
	float diff = max - min;


err  = clSetKernelArg(ko_con, 0, sizeof(cl_mem), &d_gry);
err |= clSetKernelArg(ko_con, 1, sizeof(unsigned int), &w);
err |= clSetKernelArg(ko_con, 2, sizeof(unsigned int), &h);
err |= clSetKernelArg(ko_con, 3, sizeof(unsigned int), &min);
err |= clSetKernelArg(ko_con, 4, sizeof(unsigned int), &max);

clEnqueueNDRangeKernel(commands, ko_con, 1, NULL, &global, &nthreds, 0, NULL, &event1);

clWaitForEvents (1, &event1);

 err = clEnqueueReadBuffer( commands, d_gry, CL_TRUE, 0, sizeof(unsigned char) *w*h, grayImage, 0, NULL, NULL );  
    if (err != CL_SUCCESS)
    {
        printf("Error: Failed to read output array!\n%s\n", err_code(err));
        exit(1);
    }
clGetEventProfilingInfo(event1, CL_PROFILING_COMMAND_START, sizeof(time_start), &time_start, NULL);
clGetEventProfilingInfo(event1, CL_PROFILING_COMMAND_END, sizeof(time_end), &time_end, NULL);
total_time = time_end - time_start;
printf("\nExecution time for contrast in milliseconds = %0.3f ms\n", (total_time / 1000000.0) );


//================================contrast end==================


	// Contrast enhancement
	contrast1D(grayImage.data(), grayImage.width(), grayImage.height(), histogram, HISTOGRAM_SIZE, CONTRAST_THRESHOLD);
	//contrast1DCuda

	if ( displayImages ) {
		grayImage.display("Contrast Enhanced Image");
	}
	if ( saveAllImages ) {
		grayImage.save("./contrast.bmp");
	}

	delete [] histogram;

	// Triangular smooth (convolution)
	CImg< unsigned char > smoothImage = CImg< unsigned char >(grayImage.width(), grayImage.height(), 1, 1);

	//triangularSmooth(grayImage.data(), smoothImage.data(), grayImage.width(), grayImage.height(), filter);
	//triangularSmoothCuda
	
	if ( displayImages ) {
		smoothImage.display("Smooth Image");
	}
	
	if ( saveAllImages ) {
		smoothImage.save("./smooth.bmp");
	}

clReleaseMemObject(d_img);
    clReleaseMemObject(d_gry);
    clReleaseMemObject(d_hist);
    //clReleaseMemObject(d_c);
	clReleaseEvent(event0);
	clReleaseEvent(event1);
    clReleaseProgram(program);
    clReleaseKernel(ko_vadd);
    clReleaseKernel(ko_hist);
	clReleaseKernel(ko_con);
    clReleaseCommandQueue(commands);
    clReleaseContext(context);
	
/*
free(inputImage.data());
  printf("Start 7.1  \n");  
free(grayImage.data());
printf("Start 7.2  \n");
    //free(histogram);
printf("Start 7 .3 \n");
    //free(smoothImage.data());
printf("Start 7.4  \n");*/
	return 0;
}

