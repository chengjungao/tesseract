package com.dinfo.ocr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Main { 
	 public static void main(String[] args) {
	 String directory="D:\\temp\\";  //图片文件目录
//	 String filename="Image60-1.jpg";  //图片文件
//	 String outputname="test";  //输出文件名
	 String tessPath = "F:\\Tesseract-OCR\\tesseract"; //tesseract的路径
	 String pdfpath="D:\\暴风科技：公司章程（草案）.pdf";
	 ExtractImages extractor = new ExtractImages();
	 String []strs={"",""};
	 File dirFile =new File(directory);
	 if (!dirFile.exists()) {
		 dirFile.mkdirs();
	 }
	 try {
		extractor.extractImages(pdfpath,directory,strs);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} 
	 dirFile=new File(directory);
	 File [] files =dirFile.listFiles();
	 StringBuffer str=new StringBuffer();
	 for (int i = 0; i < files.length; i++) {
		str.append(getStringformImage(directory,files[i].getName(),files[i].getName(),tessPath));
	 }
	 System.err.println(str);
	}
	 /**
	  * 从图片中读取文字
	  * @param directory
	  * @param filename
	  * @param outputname
	  * @param tessPath
	  * @return
	  */
	public static String getStringformImage(String directory,String filename,String outputname,String tessPath){
		String EOL = System.getProperty("line.separator");
		StringBuffer strB = new StringBuffer();
		 List<String> cmd = new ArrayList<String>();
		 cmd.add(tessPath); 
		 cmd.add(filename); 
		 cmd.add(outputname);
	     cmd.add("-l"); //   
	     cmd.add("chi_sim");//使用语言库为中文简体
	    
	     ProcessBuilder pb = new ProcessBuilder();
	     pb.directory(new File(directory));
	     pb.command(cmd);    
	     pb.redirectErrorStream(true);       
	     try {
			Process process = pb.start();
			  int w = process.waitFor();
			  if(w==0){    
		            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(directory+outputname+".txt"),"UTF-8"));    
		                
		            String str;    
		            while((str = in.readLine())!=null){    
		                strB.append(str).append(EOL);    
		            }    
		            in.close();    
		        }else{    
		            String msg;    
		            switch(w){    
		                case 1:    
		                    msg = "Errors accessing files.There may be spaces in your image's filename.";    
		                    break;    
		                case 29:    
		                    msg = "Cannot recongnize the image or its selected region.";    
		                    break;    
		                case 31:    
		                    msg = "Unsupported image format.";    
		                    break;    
		                default:    
		                    msg = "Errors occurred.";    
		            }    
		           
		            throw new RuntimeException(msg);    
		        }   
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	     return strB.toString();
	}
}
