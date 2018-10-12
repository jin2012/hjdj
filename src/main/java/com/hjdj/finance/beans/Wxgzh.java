package com.hjdj.finance.beans;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * 微信公众号实体类
 *
 * @author jinshan
 */
@Table(name = "wxgzh")
@Entity
public class Wxgzh implements Serializable {
    @Id
    private Integer id;
    private String appid;
    private String secret;
    // 状态：0:未使用，1:使用
    private int state;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Wxgzh{" +
                "id=" + id +
                ", appid=" + appid +
                ", secret='" + secret + '\'' +
                ", state=" + state +
                '}';
    }
}
