/** 
 * Copyright (c) 2018, Woniu1983 All Rights Reserved. 
 * 
 */ 
package cn.woniu.lib.pdf.model.derivate;

import cn.woniu.lib.pdf.model.PDFDictionary;
import cn.woniu.lib.pdf.model.PDFName;

/** 
 * @ClassName: PDFResources <br/> 
 * @Description: /Resource 对应的结构，也是PDFDictionary  <br/> 
 * 
 * @author woniu1983 
 * @date: 2018年5月15日 下午2:31:44 <br/>
 * @version  
 * @since JDK 1.6 
 */
public class PDFResources extends PDFDictionary {

	public PDFResources() {
        super();
    }
    
    public void add(PDFName key, PDFDictionary resource) {
        if (resource.size() == 0) {
            return;
        }
        
        PDFDictionary dic = getAsDict(key);
        if (dic == null) {
            put(key, resource);
        } else {
            dic.putAll(resource);
        }
    }
}
