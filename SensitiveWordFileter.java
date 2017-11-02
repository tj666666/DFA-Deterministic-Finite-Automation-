package nov.two_DFA;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author tangjia
 * @date 2017年11月2日 上午10:00:28
 * @version 1.0
 * @since jdk 1.8.0_65
 * @Description:敏感词过滤
 */
public class SensitiveWordFileter {

	private Map sensitiveWordMap = null;
	public static int minMatchType = 1;	//最小匹配规则
	public static int maxMatchType = 2; //最大匹配规则
	
	/**
	 * 构造函数,读取敏感词文件,初始化敏感词汇
	 */
	public SensitiveWordFileter() {
		sensitiveWordMap = new SensitieWordInit().initKeyword();
	}
	/**
	 * @param str	
	 * @param beginIndex
	 * @param matchType
	 * @return 如果存在,返回字符串的长度,不存在返回0
	 */
	public int CheckSensitiveWord(String str, int beginIndex, int matchType) {
		boolean endFlag = false;	//敏感词结束位标识符,用于敏感词只有一位的情况
		int matchFlag = 0;	//敏感匹配数默认为0
		char keyChar = 0;
		Map tempMap = sensitiveWordMap;
		for(int i=beginIndex;i<str.length();i++) {
			keyChar = str.charAt(i);
			tempMap = (Map) tempMap.get(keyChar);
			//存在,则判断是否为最后一个
			if(tempMap != null) {
				//找到相应key,匹配标识+1
				matchFlag++;
				if("1".equals(tempMap.get("isEnd"))) {	//如果为最后一个匹配词,则结束循环,返回匹配标识符
					endFlag = true;
					if(SensitiveWordFileter.minMatchType == matchType) {	//最小规则直接返回,最大规则还需要继续查找
						break;
					}
				}
			}else {	//不存在,直接返回
				break;
			}
		}
		if(matchFlag < 2 || !endFlag) {		//长度必须大于等于2,为词
			matchFlag = 0;
		}
		return matchFlag;
	}
	
	/**
	 * 判断文字是否包含敏感词汇
	 * @param txt 文字
	 * @param matchType 匹配规则：1.最小匹配规则 2.最大匹配规则
	 * @return 包含则return true,不包含则return false
	 */
	
	public boolean isCOntainSensitiveWord(String str,int matchType) {
		boolean flag = false;
		for(int i= 0;i<str.length();i++) {
			int matchFlag = CheckSensitiveWord(str,i,matchType);	//判断是否包含敏感字符
			if(matchFlag > 0) {
				flag = true;
			}
		}
		return flag;
	}
	
	/**
	 * 
	 * @param newLine
	 * @param matchType 匹配规则: 1.最小匹配规则   2.最大匹配规则
	 * @return
	 */
	public Set<String> getSensitiveWord(String str,int matchType) {
		Set<String> sensitiveWordSet = new HashSet<>();
		for(int i=0;i<str.length();i++) {
			int length = CheckSensitiveWord(str,i,matchType);	//判断是否包含敏感字符
			//存在,加入Set中
			if(length > 0) {
				sensitiveWordSet.add(str.substring(i,i+length));
				i = i + length - 1;		//减1的原因,因为for会自增
			}
		}
		return sensitiveWordSet;
	}
	
	/**
	 * @param str
	 * @param matchType
	 * @param replaceChar 替换字符,默认为*
	 * @return
	 */
	public String replaceSensitiveWord(String str,int matchType,String replaceChar) {
		String result = str;
		Set<String> set = getSensitiveWord(str, matchType);		//获取所有敏感词
		Iterator<String> iterator = set.iterator();
		String key = null;
		String replaceStr = null;
		while(iterator.hasNext()) {
			key = iterator.next();
			replaceStr = getReplaceChars(replaceChar, key.length());
			result = result.replaceAll(key, replaceStr);
		}
		return result;
	}
	
	/**
	 * @Description:获取替换字符串
	 * @param replaceChar
	 * @param length
	 * @return
	 */
	public String getReplaceChars(String replaceChar, int length) {
		String result = replaceChar;
		for(int i=1; i<length;i++){
			result += replaceChar;
		}
		return result;
	}
	
	public static void main(String[] args) {
		SensitiveWordFileter fileter = new SensitiveWordFileter();
		System.out.println("敏感词数量: "+fileter.sensitiveWordMap.size());
		String string = "枪械炸药硝铵火药燃烧瓶做证件太多的伤感情怀也许只局限于饲养基地 荧幕中的情节，主人公尝试着去用某种方式渐渐的很潇洒地释自杀指南怀那些自己经历的伤感。"
				+ "办理证件然后法轮功 我们的扮演的角色就是跟随着主人公的喜红客联盟 怒哀乐而过于牵强的把自己的情感也附加于银幕情节中，然后感动就流泪，"
				+ "难过就躺在某一个人的怀里尽情的阐述心扉或者手机卡复制器一个人一杯红酒一部电影在夜三级片 深人静的晚上，关上电话静静的发呆着。";
		String replacedStr = fileter.replaceSensitiveWord(string, 1, "*");
		System.out.println(replacedStr);
		System.out.println("待检测语句子数: "+string.length());
		long beginTime = System.nanoTime();
		Set<String> set = fileter.getSensitiveWord(string, 1);
		long endTime = System.nanoTime();
		System.out.println("语句中包含敏感词数量: "+set.size()+", 包含: "+set);
		System.out.println("总耗时: "+(endTime-beginTime));
	}
}
