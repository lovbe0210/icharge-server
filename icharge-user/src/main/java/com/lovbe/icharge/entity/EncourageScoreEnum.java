package com.lovbe.icharge.entity;

/**
 * @Author: lovbe0210
 * @Date: 2025/3/18 0:23
 * @Description: 激励加分值
 */
public enum EncourageScoreEnum {
    LOGIN(1, 5, "每日登录");

    private int scene;
    private int score;
    private String desc;

    EncourageScoreEnum(int scene, int score, String desc) {
        this.scene = scene;
        this.score = score;
        this.desc = desc;
    }


}
