package com.dinfo.ocr;

import java.io.File;  
import java.io.IOException;  
import java.util.Iterator;  
import java.util.List;  
import java.util.Map;  
  
import org.apache.pdfbox.pdmodel.PDDocument;  
import org.apache.pdfbox.pdmodel.PDPage;  
import org.apache.pdfbox.pdmodel.PDResources;  
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.encryption.AccessPermission;  
import org.apache.pdfbox.pdmodel.encryption.StandardDecryptionMaterial;  
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;  
 /**
  * 功能描述：从pdf中抽取图片
  * @author Administrator
  *
  */
public class ExtractImages  
{  
    private int imageCounter = 1;  
  
    private static final String PASSWORD = "-password";  
    private static final String PREFIX = "-prefix";  
  
    ExtractImages()  
    {  
    }  
  
 
    public static void main( String[] args ) throws Exception  
    {  
        ExtractImages extractor = new ExtractImages();
        String []strs={"",""};
        String pdfFile = "D:\\爱普股份：实际控制人关于公司股票交易异常波动的问询函的回函15-3-31.pdf"; 
        String outdir="D:\\";
        extractor.extractImages(pdfFile,outdir,strs);  
    }  
  /**
   * 
   * @param pdfFile
   * @param outdir
   * @param args
   * @throws Exception
   */
    public void extractImages( String pdfFile,String outdir, String[] args) throws Exception  
    {  
        if( args.length < 1 || args.length > 3 )  
        {  
            usage();  
        }  
        else  
        {  
            String password = "";  
            String prefix = null;  
            for( int i=0; i<args.length; i++ )  
            {  
                if( args[i].equals( PASSWORD ) )  
                {  
                    i++;  
                    if( i >= args.length )  
                    {  
                        usage();  
                    }  
                    password = args[i];  
                }  
                else if( args[i].equals( PREFIX ) )  
                {  
                    i++;  
                    if( i >= args.length )  
                    {  
                        usage();  
                    }  
                    prefix = args[i];  
                }  
                else  
                {  
                    if( pdfFile == null )  
                    {  
                        pdfFile = args[i];  
                    }  
                }  
            }  
            if(pdfFile == null)  
            {  
                usage();  
            }  
            else  
            {  
                if( prefix == null && pdfFile.length() >4 )  
                {  
                    prefix = pdfFile.substring( 0, pdfFile.length() -4 );  
                }  
  
                PDDocument document = null;  
  
                try  
                {  
                    document = PDDocument.load( pdfFile );  
  
                    if( document.isEncrypted() )  
                    {  
  
                        StandardDecryptionMaterial spm = new StandardDecryptionMaterial(password);  
                        document.openProtection(spm);  
                        AccessPermission ap = document.getCurrentAccessPermission();  
  
  
                        if( ! ap.canExtractContent() )  
                        {  
                            throw new IOException(  
                                "Error: You do not have permission to extract images." );  
                        }  
                    }  
  
                    List pages = document.getDocumentCatalog().getAllPages();  
                    Iterator iter = pages.iterator();  
                    while( iter.hasNext() )  
                    {  
                        PDPage page = (PDPage)iter.next();  
                        PDResources resources = page.getResources();  
                        Map images = resources.getImages();  
                        if( images != null )  
                        {  
                            Iterator imageIter = images.keySet().iterator();  
                            while( imageIter.hasNext() )  
                            {  
                                String key = (String)imageIter.next();  
                                PDXObjectImage image = (PDXObjectImage)images.get( key );  
                                String name = getUniqueFileName( key, image.getSuffix() );  
                                System.out.println( "Writing image:" + name );  
                               // PDStream pdfstream=image.getPDStream();
                                
                                image.write2file(outdir+name);  
                            }  
                        }  
                    }  
                }  
                finally  
                {  
                    if( document != null )  
                    {  
                        document.close();  
                    }  
                }  
            }  
        }  
    }  
  
    private String getUniqueFileName( String prefix, String suffix )  
    {  
        String uniqueName = null;  
        File f = null;  
        while( f == null || f.exists() )  
        {  
            uniqueName = prefix + "-" + imageCounter;  
            f = new File( uniqueName + "." + suffix );  
            imageCounter++;  
        }  
        return uniqueName;  
    }  
  
    /** 
     * This will print the usage requirements and exit. 
     */  
    private static void usage()  
    {  
        System.err.println( "Usage: java org.apache.pdfbox.ExtractImages [OPTIONS] <PDF file>\n" +  
            "  -password  <password>        Password to decrypt document\n" +  
            "  -prefix  <image-prefix>      Image prefix(default to pdf name)\n" +  
            "  <PDF file>                   The PDF document to use\n"  
            );  
        System.exit( 1 );  
    }  
  
}  
