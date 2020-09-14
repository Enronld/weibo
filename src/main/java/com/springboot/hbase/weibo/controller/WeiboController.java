package com.springboot.hbase.weibo.controller;

import com.alibaba.fastjson.JSONObject;
import com.springboot.hbase.weibo.pojo.User;
import com.springboot.hbase.weibo.pojo.Weibo;
import com.springboot.hbase.weibo.service.UserService;
import com.springboot.hbase.weibo.service.WeiboService;
import com.springboot.hbase.weibo.util.FileOption;
import com.springboot.hbase.weibo.util.TimeStamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Controller
public class WeiboController {
    @Autowired
    private WeiboService weiboService;
    @Autowired
    private UserService userService;

    //接收图片
    @RequestMapping(value = "uploadImage", method = RequestMethod.POST)
    @ResponseBody
    public String uploadImage(MultipartFile file) {
        File upload = null;
        try {
            upload = FileOption.multipartFileToFile(file);
            String name =  weiboService.savePhoto(upload);
            return "http://localhost:8080/weibo/image/" + name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //发微博
    @RequestMapping(value = "sendWeibo", method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String sendWeibo(@RequestBody Weibo weibo) {
            JSONObject result = new JSONObject();
            String rowKey = TimeStamp.timeToTimeStamp(weibo.getTime()) + "_" + weibo.getUser();
            if( weiboService.addWeibo(rowKey,weibo.getContent(),weibo.getImg())){
                result.put("status", "success");
                result.put("detail","发布成功！");
                return result.toJSONString();
            }else{
                result.put("status", "failure");
                result.put("detail","发布失败，请重试！");
                return result.toJSONString();
            }

    }

    //获取所有微博
    @RequestMapping(value = "getAllWeibo", method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getAllWeibo() {
        JSONObject result = new JSONObject();
        List<Weibo> allWeb = new ArrayList<>();
        allWeb = weiboService.getAllWeibo();
        if(allWeb != null){
            result.put("status", "success");
            result.put("detail","获取成功！");
            result.put("data",allWeb);
            return result.toJSONString();
        }else{
            result.put("status", "failure");
            result.put("detail","获取失败，请重试！");
            return result.toJSONString();
        }
    }

    //获取关注的人发布的微博
    @RequestMapping(value = "getFollowWeibo", method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getFollowWeibo(@RequestBody User user) {
        JSONObject result = new JSONObject();
        String rowKey = user.getPhone();

        List<Weibo> allWeb = weiboService.getAllWeibo();
        List<Weibo> followWeb = new ArrayList<>();

        try {
            //获取用户关注的人
            List<User> followUser = userService.getFollowWho(rowKey);
            for (int i = 0;i < allWeb.size();i++){
                for(int j = 0; j < followUser.size();j++){
                    if (allWeb.get(i).getUser().equals(followUser.get(j).getPhone())){
                        followWeb.add(allWeb.get(i));
                    }
                }
            }
            result.put("status", "success");
            result.put("detail","获取成功！");
            result.put("data",followWeb);
            return result.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "failure");
            result.put("detail","获取失败，请重试！");
            return result.toJSONString();

        }
    }

    //获取被关注的人发布的微博
    @RequestMapping(value = "getFollowedWeibo", method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getFollowedWeibo(@RequestBody User user) {
        JSONObject result = new JSONObject();
        String rowKey = user.getPhone();

        List<Weibo> allWeb = weiboService.getAllWeibo();
        List<Weibo> followWeb = new ArrayList<>();

        try {
            //获取用户关注的人
            List<User> followUser = userService.getFollowedWho(rowKey);
            for (int i = 0;i < allWeb.size();i++){
                for(int j = 0; j < followUser.size();j++){
                    if (allWeb.get(i).getUser().equals(followUser.get(j).getPhone())){
                        followWeb.add(allWeb.get(i));
                    }
                }
            }
            result.put("status", "success");
            result.put("detail","获取成功！");
            result.put("data",followWeb);
            return result.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "failure");
            result.put("detail","获取失败，请重试！");
            return result.toJSONString();

        }
    }

    //获取当前用户发布的微博
    @RequestMapping(value = "getUserWeibo", method = RequestMethod.POST,produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String getUserWeibo(@RequestBody User user) {
        JSONObject result = new JSONObject();
        String rowKey = user.getPhone();

        List<Weibo> allWeb = weiboService.getAllWeibo();
        List<Weibo> userWeb = new ArrayList<>();

        try {
            for (int i = 0;i < allWeb.size();i++){
                    if (allWeb.get(i).getUser().equals(rowKey)){
                        userWeb.add(allWeb.get(i));
                }
            }
            result.put("status", "success");
            result.put("detail","获取成功！");
            result.put("data",userWeb);
            return result.toJSONString();
        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "failure");
            result.put("detail","获取失败，请重试！");
            return result.toJSONString();

        }
    }
}
