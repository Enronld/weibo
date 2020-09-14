package com.springboot.hbase.weibo.controller;

import com.alibaba.fastjson.JSONObject;
import com.springboot.hbase.weibo.pojo.User;
import com.springboot.hbase.weibo.pojo.UserVO;
import com.springboot.hbase.weibo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "login", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    //用户名密码登录
    public String login(@RequestBody User user1) {
        User user = new User();
        JSONObject result = new JSONObject();
        System.out.println(user1.getPhone());
        try {
            user = userService.ifUserExit(user1.getPhone());
            if (user != null){
                if (!userService.getUserPwd(user.getPhone()).equals(user1.getPwd())){
                    result.put("status", "failure");
                    result.put("detail","密码错误，登录失败！");
                    return result.toJSONString();
                }else{
                    result.put("status", "success");
                    result.put("detail","登录成功！");
                    result.put("data",user);
                    return result.toJSONString();
                }
            }else{
                result.put("status", "failure");
                result.put("detail","该用户不存在！");
                return result.toJSONString();
            }
        } catch (Exception e) {
            result.put("status", "failure");
            result.put("detail","内部错误，请重试！");
            return result.toJSONString();
        }
    }

    @RequestMapping(value = "addUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    //新增用户
    public String addUser(@RequestBody User user) {
        JSONObject result = new JSONObject();

        try {
            List<User> users = userService.getAllUser();
            for (int i = 0; i < users.size();i++){
                if (user.getPhone().equals(users.get(i).getPhone())){
                    result.put("status", "failure");
                    result.put("detail","该账号已注册过，请更换账号注册！");
                    return result.toJSONString();
                }else if(user.getName().equals(users.get(i).getName())){
                    result.put("status", "failure");
                    result.put("detail","该微博名称已被使用，请更换微博名称！");
                    return result.toJSONString();
                }
            }
            userService.addUser(user.getPhone(),user.getName(),user.getPwd());
            result.put("status", "success");
            result.put("detail","注册成功！");
            return result.toJSONString();

        } catch (Exception e) {
            result.put("status", "failure");
            result.put("detail","内部错误，请重试！");
            return result.toJSONString();
        }
    }

    @RequestMapping(value = "getAllUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    //获取所有用户
    public String getAllUser(){
        JSONObject result = new JSONObject();
        try {
            List<User> users = userService.getAllUser();
            result.put("status", "success");
            result.put("detail","查找成功！");
            result.put("data",users);
            return result.toJSONString();

        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "failure");
            result.put("detail","查找失败，请刷新！");
            return result.toJSONString();

        }

    }

    @RequestMapping(value = "getUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    //根据用户名模糊查询
    public String getUser(@RequestBody User user){
        JSONObject result = new JSONObject();
        try {
            List<User> users = userService.getAllUser();
            List<User> find = new ArrayList<>();
            for (int i = 0; i < users.size(); i++){
                if (users.get(i).getName().contains(user.getName())){
                    find.add(users.get(i));
                }
            }
            result.put("status", "success");
            result.put("detail","查找成功！");
            result.put("data",find);
            return result.toJSONString();

        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "failure");
            result.put("detail","查找失败，请刷新！");
            return result.toJSONString();

        }

    }

    @RequestMapping(value = "getFollowUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    //查询关注的用户
    public String getFollowUser(@RequestBody User user){
        String rowkey = user.getPhone();
        JSONObject result = new JSONObject();
        try {
            List<User> users = userService.getFollowWho(rowkey);
            result.put("status", "success");
            result.put("detail","查找成功！");
            result.put("data",users);
            return result.toJSONString();

        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "failure");
            result.put("detail","查找失败，请刷新！");
            return result.toJSONString();

        }

    }

    @RequestMapping(value = "getFollowedUser", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    //查询被谁关注
    public String getFollowedUser(@RequestBody User user){
        String rowkey = user.getPhone();
        JSONObject result = new JSONObject();
        try {
            List<User> users = userService.getFollowedWho(rowkey);
            result.put("status", "success");
            result.put("detail","查找成功！");
            result.put("data",users);
            return result.toJSONString();

        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "failure");
            result.put("detail","查找失败，请刷新！");
            return result.toJSONString();

        }

    }

    @RequestMapping(value = "ifUserFollow", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    //查询两个用户是否相互关注
    public String ifUserFollow(@RequestBody UserVO uservo){
        JSONObject result = new JSONObject();

        try {
            if (userService.ifFollow(uservo.getPhone1(),uservo.getPhone2())){
                result.put("status", "success");
                result.put("detail","关注");
                return result.toJSONString();
            }else{
                result.put("status", "success");
                result.put("detail","不关注");
                return result.toJSONString();
            }

        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "failure");
            result.put("detail","查找失败，请刷新！");
            return result.toJSONString();

        }

    }

    @RequestMapping(value = "userFollow", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    //用户A关注用户B
    public String userFollow(@RequestBody UserVO uservo){
        JSONObject result = new JSONObject();

        try {
                userService.userFollowUser(uservo.getPhone1(),uservo.getPhone2());
                result.put("status", "success");
                result.put("detail","关注成功");
                return result.toJSONString();

        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "failure");
            result.put("detail","关注失败，请重试！");
            return result.toJSONString();

        }

    }

    @RequestMapping(value = "userUnFollow", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    @ResponseBody
    //用户A取消关注用户B
    public String userUnFollow(@RequestBody UserVO uservo){
        JSONObject result = new JSONObject();

        try {
            userService.userUnfollowUser(uservo.getPhone1(),uservo.getPhone2());
            result.put("status", "success");
            result.put("detail","取消关注成功");
            return result.toJSONString();

        } catch (Exception e) {
            e.printStackTrace();
            result.put("status", "failure");
            result.put("detail","取消关注失败，请重试！");
            return result.toJSONString();

        }

    }
}
