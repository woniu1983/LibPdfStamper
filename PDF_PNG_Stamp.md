PDF增量更新的方式插入可见图片水印(PNG 带Alpha通道)

1. 原PDF文件读取写入新文件NewFile

2. 读取原PDF文件中的Dictionary
   - 每一页对应的MediaBox所在的obj
   - xref--trailer
   - startxref对应的数值， 例如： 260904
   - /Size, 例如： 14， 指导添加下一个Obj时需要的编号Number必须>=该值
   
3. 读取PNG图片
   - 读取iDAT下的图像数据--使用Deflater方式压缩数据， 记为IMDATA
   - 读取Alpha通道的图像数据(透明)--使用Deflater方式压缩数据， 记为SMaskDATA
   - 以上两个必须作为独立的Obj写入到PDF文件   
  
   
4. 写入Image追加的剩余的PDF命令(Obj)，使用Deflater方式压缩数据
   - 规则： ？？？？？

5. 写入/Page 类型的Obj
   - 读取原来的/Page 类型的Obj， 例如：
	4 0 obj
    <</Rotate 0 /MediaBox [0.0 0.0 596.16 842.04 ] /Type /Page /Contents 8 0 R /Parent 2 0 R /Resources <</XObject <</scan_img0 6 0 R >> /ProcSet [/PDF /Text /ImageB /ImageC /ImageI ] >> >>
	endobj 
   - 加入相关参数后添加到后面， Obj号码不变， 例如：
    4 0 obj
	<</Rotate 0/MediaBox[0.0 0.0 596.16 842.04]/Type/Page/Contents[18 0 R 8 0 R 19 0 R]/Parent 2 0 R/Resources<</XObject<</scan_img0 6 0 R/Xi0 14 0 R/Xi1 15 0 R>>/ProcSet[/PDF/Text/ImageB/ImageC/ImageI]>>>>
	endobj
   
6. 写xref (XPdfCrossReference)
   - 根据追加的Obj写入新的xref
   - 规则： ？？？？？
   
7. 写trailer
   - 读取原文档最后的trailer， 例如  
		trailer
		<</Info 1 0 R /Root 3 0 R /Size 14 >>
   - 读取原文档最后的startxref后面一行的数值xxxxx， 例如：
      startxref
	  252354
   -  合并最新的写入： 规则： ？？？？？
      <</Size 20/Root 3 0 R/Info 1 0 R/ID [<09f191b3ad7e4ef42b145d11be7d4873><51c07ca0fb3be7a43778cf39aefa87f3>]/Prev 252354>>
	  
   - 根据最终的Size写入startxref， 例如： 
        startxref
        260917
		
8. 写入%%EOF




*iTextPDF
  1. PdfImage extends PdfStream extends PdfDictionary extends PdfObject implements Serializable 
  2. PdfName extends PdfObject implements Comparable<PdfName>  
 

