package com.stratagile.pnrouter.entity;

import java.util.List;

public class PublishBlock {

    /**
     * account : qlc_3t1mwnf8u4oyn7wc7wuptnsfz83wsbrubs8hdhgkty56xrrez4x7fcttk5f3
     * type : email
     * id : 11@qq.com
     * pubkey : 0ae6c2ade291b398c3dc4b4c0164bf72813d6150b25da69371bb3008e4942211
     * fee : 500000000
     * verifiers : ["qlc_1bwjtpipkzc7aj6hmuodncjmfsb4tou9word8bj9jxcm68cheipad54q66xe","qlc_3hw8s1zubhxsykfsq5x7kh6eyibas9j3ga86ixd7pnqwes1cmt9mqqrngap4"]
     */

    private String account;
    private String type;
    private String id;
    private String pubkey;
    private String fee;
    private List<String> verifiers;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPubkey() {
        return pubkey;
    }

    public void setPubkey(String pubkey) {
        this.pubkey = pubkey;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public List<String> getVerifiers() {
        return verifiers;
    }

    public void setVerifiers(List<String> verifiers) {
        this.verifiers = verifiers;
    }
}
