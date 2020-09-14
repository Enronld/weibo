package com.springboot.hbase.weibo.service;

import com.springboot.hbase.weibo.dao.HbaseMapper;
import com.springboot.hbase.weibo.pojo.User;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {
    public static String tableName = "Enron:User";

    //判断用户名是否存在
    public User ifUserExit(String Phone) throws Exception {
        User user = new User();
        HashMap<String,String> hm = HbaseMapper.getCell(tableName,Phone,"info","name");
        if (hm == null){
            user = null;
        }else{
            user.setPhone(hm.get("rowKey"));
            user.setName(hm.get("value"));
        }
        return user;

    }
    //根据用户名获取密码
    public String getUserPwd(String Phone) throws Exception {

        HashMap<String,String> hm = HbaseMapper.getCell(tableName,Phone,"info","pwd");
        String pwd = null;
        if (hm == null){

        }else{
            pwd = hm.get("value");
        }
        return pwd;

    }

    //新增用户
    public void addUser(String phone,String name, String pwd) throws Exception {
        HbaseMapper.put(tableName,phone,"info","name",name);
        HbaseMapper.put(tableName,phone,"info","pwd",pwd);
    }

    //查询所有用户
    public List<User> getAllUser() throws Exception {
        List<HashMap<String,String>> users = HbaseMapper.scanAll(tableName,"info","name");
        List<User> alluser = new ArrayList<>();
        for (int i = 0;i < users.size();i++){
            User user = new User();
            user.setPhone(users.get(i).get("rowKey"));
            user.setName(users.get(i).get("value"));
            alluser.add(user);
            System.out.println(user.getPhone());
        }

        return alluser;
    }

    //查询一个用户关注了谁
    public List<User> getFollowWho(String rowKey) throws Exception {
         List<HashMap<String,String>> allFollow = HbaseMapper.getColumnFamilyCell(tableName,rowKey,"follow");
         List<User> followUser = new ArrayList<>();
         if (allFollow != null){
             for (int i = 0;i < allFollow.size();i++){
                 User user = new User();
                 user.setPhone(allFollow.get(i).get("column"));
                 user.setName(allFollow.get(i).get("value"));
                 followUser.add(user);
                 System.out.println(user.getPhone());
             }
         }

         return followUser;
    }

    //查询一个用户被谁关注
    public List<User> getFollowedWho(String rowKey) throws Exception {
        List<HashMap<String,String>> allFollowed = HbaseMapper.getColumnFamilyCell(tableName,rowKey,"followed");
        List<User> followedUser = new ArrayList<>();
        if (allFollowed != null){
            for (int i = 0;i < allFollowed.size();i++){
                User user = new User();
                user.setPhone(allFollowed.get(i).get("column"));
                user.setName(allFollowed.get(i).get("value"));
                followedUser.add(user);
                System.out.println(user.getPhone());
            }
        }


        return followedUser;
    }

    //判断一个用户是否被另一个用户关注
    public boolean ifFollow(String rowKey1,String rowKey2) throws Exception {
        List<HashMap<String,String>> allFollow = HbaseMapper.getColumnFamilyCell(tableName,rowKey1,"follow");
        if (allFollow != null){
            for (int i = 0;i < allFollow.size();i++){
                if (allFollow.get(i).get("column").equals(rowKey2)){
                    return true;
                }

            }
        }

        return false;
    }

    //用户A关注用户B
    public void userFollowUser(String rowKey1, String rowKey2) throws Exception {
        HbaseMapper.put(tableName, rowKey1, "follow", rowKey2, HbaseMapper.getCell(tableName, rowKey2, "info", "name").get("value"));
        HbaseMapper.put(tableName, rowKey2, "followed", rowKey1, HbaseMapper.getCell(tableName, rowKey1, "info", "name").get("value"));
    }
    //用户A取消关注用户B
    public void userUnfollowUser(String rowKey1, String rowKey2) throws Exception {
        HbaseMapper.deleteCellData(tableName, rowKey1, "follow", rowKey2);
        HbaseMapper.deleteCellData(tableName, rowKey2, "followed", rowKey1);
    }

}
