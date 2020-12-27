package cn.edu.tju.simulation.SCS.paramaters;

import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import cn.edu.tju.simulation.IF.files.SingleContent;


/**
 * Unfinished
 * 
 * @author Wenkai Li ,School of Computer Science and Technology ,Tianjin University 
 *
 */
public class ContentWriter {
	
    public Boolean creatContentXML(){  
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		Random r = new Random();
		try {
			db = dbf.newDocumentBuilder();
			 //����һ��Dom��  
	        Document document = db.newDocument();  
	        //ȥ��standalone="no"����,˵��ֻ��һ���򵥵�xml,û������DTD(document type definition�ĵ����Ͷ���)�淶  
	        document.setXmlStandalone(true);  
	        //����Location���ڵ�  
	        Element rootElement = document.createElement("containt"); 
	        List<SingleContent> fileSizeList = new ContentReader().read();
	        //����CountryRegion�ڵ�  
	        for(int i = 0;i<fileSizeList.size();i++){
	            Element type = document.createElement("media");
	            type.setAttribute("id",String.valueOf(i)); 
	            
	            Element name = document.createElement("name"); 
	            name.setTextContent(String.valueOf(i));
	            
	            Element size = document.createElement("size");
	            size.setTextContent((String.valueOf((int)((fileSizeList.get(i).getSize())*(r.nextFloat()+0.1)))));
	            
	            Element popularity = document.createElement("popularity");
	            popularity.setTextContent((String.valueOf((int)((fileSizeList.get(i).getPopularity())))));
	            
	            type.appendChild(name); 
	            type.appendChild(size);
	            type.appendChild(popularity);
	            rootElement.appendChild(type);  
	            
	        }
	          
	        //���������ӽڵ��rootElement��ӵ�document��  
	        document.appendChild(rootElement);  
	        //ʵ���������࣬�����಻��ʹ��new�ؼ���ʵ������������  
	        TransformerFactory transFactory = TransformerFactory.newInstance();
	        //����transformer����  
            Transformer transformer = transFactory.newTransformer();  
            //���û���  
            transformer.setOutputProperty(OutputKeys.INDENT, "Yes");  
            //����ת��,�������ǳ����࣬Ҫ�õ�ȴ�Ǹ������һЩ�࣬��Щ�����������һЩ���ɵġ�  
            transformer.transform(new DOMSource(document), new StreamResult("data/Containt.xml"));
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} 

		return true;  
    }
    
    public void saveContentXML(Vector<Vector<String>>tableColumn ){		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			 //����һ��Dom��  
	        Document document = db.newDocument();  
	        //ȥ��standalone="no"����,˵��ֻ��һ���򵥵�xml,û������DTD(document type definition�ĵ����Ͷ���)�淶  
	        document.setXmlStandalone(true);  
	        //����Location���ڵ�  
	        Element rootElement = document.createElement("containt"); 
	        //����CountryRegion�ڵ�  
			for(int i = 0 ;i<tableColumn.size();i++){
				Vector<String> vector = tableColumn.get(i);
	            Element type = document.createElement("media");
	            type.setAttribute("id",vector.get(0)); 
	            
	            Element name = document.createElement("name"); 
	            name.setTextContent(vector.get(0));
	            
	            Element size = document.createElement("size");
	            size.setTextContent(vector.get(1));
	            
	            Element popularity = document.createElement("popularity");
	            popularity.setTextContent(vector.get(2));
	            
	            type.appendChild(name); 
	            type.appendChild(size);
	            type.appendChild(popularity);
	            rootElement.appendChild(type); 
			}

	          
	        //���������ӽڵ��rootElement��ӵ�document��  
	        document.appendChild(rootElement);  
	        //ʵ���������࣬�����಻��ʹ��new�ؼ���ʵ������������  
	        TransformerFactory transFactory = TransformerFactory.newInstance();
	        //����transformer����  
            Transformer transformer = transFactory.newTransformer();  
            //���û���  
            transformer.setOutputProperty(OutputKeys.INDENT, "Yes");  
            //����ת��,�������ǳ����࣬Ҫ�õ�ȴ�Ǹ������һЩ�࣬��Щ�����������һЩ���ɵġ�  
            transformer.transform(new DOMSource(document), new StreamResult("data/Containt.xml"));  
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} 

    }
    
    public static void main(String[] args) {
		new ContentWriter().creatContentXML();
	}

}
