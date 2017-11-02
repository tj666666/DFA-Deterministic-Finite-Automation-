package nov.two_DFA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author tangjia
 * @date 2017年11月2日 上午9:05:16
 * @version 1.0
 * @since jdk 1.8.0_65
 * @Description:初始化敏感词过滤库,将敏感词加入到HashMap中,构建DFA算法模型
 */
public class SensitieWordInit {
	
	@SuppressWarnings("rawtypes")
	public HashMap sensitiveWordMap;
	
	public SensitieWordInit() {
		super();
	}
	
	/**
	 * 读取敏感词库中的内容,将内容添加到set集合中
	 * 问题:为什么要用hashSet转hashMap,先利用hashSet的添加有序性添加敏感词,再利用每个敏感词里的逻辑生成针对每一个敏感词的tempMap
	 * @return
	 * @throws Exception
	 */
	private Set<String> readSensitiveWordFile() throws IOException {
		Set<String> set = null;
		File file = new File("G:\\SensitiveWord.txt");
		InputStreamReader reader = new InputStreamReader(new FileInputStream(file));
		try {
			//文件流是否存在
			if(file.isFile() && file.exists()) {
				set = new HashSet<>();
				BufferedReader bufferedReader = new BufferedReader(reader);
				String newLine = null;
				while((newLine = bufferedReader.readLine()) != null) {
					set.add(newLine);
				}
			}else {
				throw new FileNotFoundException("敏感词文件不存在");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(reader != null) {
				try {
					reader.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
		return set;
	}
	
	public Map initKeyword() {
		try {
			//读取敏感词库
			Set<String> keywordSet = readSensitiveWordFile();
			//将敏感词库加入到HashMap中
			addSensitiveWordToHashMap(keywordSet);
			//spring获取application,然后application.setAttribute("snesitiveWordMap",sensitiveWordMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sensitiveWordMap;
	}
	
	/**
	 * 读取敏感词库,将敏感词放入HashSet中,构建一个DFA算法模型：
	 * 中  = {
	 * 		isEnd = 0;
	 * 		国  = {
	 * 			isEnd = 1;
	 * 			P = {
	 * 				isEnd = 0;
	 * 				民 = {
	 * 					isEnd = 1;
	 * 					}
	 * 				}
	 * 			}
	 * 		}
	 * 鹬  = {
	 * 		isEnd = 0;
	 * 		蚌 = {
	 * 			isEnd = 0;
	 * 			相 = {
	 * 				isEnd = 0;
	 * 				争 = {
	 * 					isEnd = 1;
	 * 					}
	 * 				}
	 * 			}
	 * 		}
	 * @param keywordSet 敏感词库
	 */
	
	private void addSensitiveWordToHashMap(Set<String> keywordSet) {
		sensitiveWordMap  = new HashMap<>(keywordSet.size());	//初始化敏感词容器,减少扩容操作
		String key = null;
		Map tempMap = null;
		Map<String, String> newWordMap = null;
		//迭代keywordSet
		Iterator<String> iterator = keywordSet.iterator();
		while(iterator.hasNext()) {
			key = iterator.next();
			tempMap = sensitiveWordMap;
			for(int i=0;i<key.length();i++) {
				char keyChar = key.charAt(i);
				Object wordMap = tempMap.get(keyChar);	//获取对应的map
				if(wordMap != null) {
					tempMap = (Map)wordMap;
				}else {	//如果不存在,则构建新的map,同时将isEnd设置为0,因为不是最后一个字符
					newWordMap = new HashMap<>();
					newWordMap.put("isEnd", "0");
					tempMap.put(keyChar, newWordMap);
					tempMap = newWordMap;
				}
				if(i == key.length() - 1) {
					tempMap.put("isEnd", "1"); //将最后一个元素修改为isEnd=1,不过个人有个想法,可以在遍历的时候只遍历到key.length()-2,在循环体外部再加一次tempMap.put("isEnd","1");
				}
			}
		}
	}
	
	
}
