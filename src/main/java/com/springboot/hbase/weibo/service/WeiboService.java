package com.springboot.hbase.weibo.service;

import com.springboot.hbase.weibo.dao.HbaseMapper;
import com.springboot.hbase.weibo.pojo.Weibo;
import com.springboot.hbase.weibo.util.TimeStamp;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

@Service
public class WeiboService {
    public static String tableName = "Enron:Weibo";
    public static String tableName1 = "Enron:User";

    //上传图片
    public String savePhoto(File upload) {
        // TODO Auto-generated method stub
        String name = "";
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 6; i++) {
            int number = random.nextInt(50);
            sb.append(str.charAt(number));
        }
        name = sb.toString();
        String path="D:\\HBaseImg\\"+name+".jpg";

        //2.保存照片
        File file=new File(path);
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return "0";
            }
        }
        if(upload==null) {
            System.out.println("upload is null");
        }
        if(!file.exists()) {
            System.out.println("file is null");
        }
        try{
            FileUtils.copyFile(upload,file);
        }catch (IOException e){
            e.printStackTrace();
            return "0";
        }
        return name+".jpg";
    }

    //添加微博
    public boolean addWeibo(String rowKey,String content, String img){
        try {
            HbaseMapper.put(tableName,rowKey,"info","content",content);
            HbaseMapper.put(tableName,rowKey,"info","img",img);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //获取所有微博
    public List<Weibo> getAllWeibo(){
        List<Weibo> weibo = new ArrayList();
        try {
            List<HashMap<String,String>> all = HbaseMapper.scanAll2(tableName,"info");
            for (int i = 0; i<all.size()/2; i++){
                Weibo weibo1 = new Weibo();
                String userTime = all.get(i).get("rowKey");
                String[] split = userTime.split("_");
                weibo1.setUser(split[1]);
                weibo1.setTime(TimeStamp.timeStampToTime(split[0]));
                weibo1.setName(HbaseMapper.getCell(tableName1,split[1],"info","name").get("value"));
                int j = i*2;
                weibo1.setContent(all.get(j).get("value"));
                weibo1.setImg(all.get(j+1).get("value"));
                weibo.add(weibo1);
            }
//            List<HashMap<String,String>> allContent = HbaseMapper.scanAll(tableName,"info","content");
//            List<HashMap<String,String>> allImg = HbaseMapper.scanAll(tableName,"info","img");
//            for (int i = 0; i<allContent.size(); i++){
//                Weibo weibo1 = new Weibo();
//                String userTime = allContent.get(i).get("rowKey");
//                String[] split = userTime.split("_");
//                weibo1.setUser(split[1]);
//                System.out.println(split[1]);
//                System.out.println(HbaseMapper.getCell(tableName1,split[1],"info","name").get("value"));
//                weibo1.setName(HbaseMapper.getCell(tableName1,split[1],"info","name").get("value"));
//                weibo1.setTime(TimeStamp.timeStampToTime(split[0]));
//                weibo1.setContent(allContent.get(i).get("value"));
//                for (int j = 0; j<allImg.size(); j++){
//                    if (allImg.get(j).get("rowKey").equals(userTime)){
//                        weibo1.setImg(allImg.get(j).get("value"));
//                        break;
//                    }
//                }
//                weibo.add(weibo1);
//            }
            return weibo;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
