/*
 * Copyright 2018 Lake Zhang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.smailnet.eamil;

/**
 * 邮件相关数量
 *
 * @author zl
 * @version 2.3
 */
public class EmailCount {


    private int totalCount;        //Inbox消息总数
    private int unReadCount;        //Inbox未读数量

    private int nodeTotalCount;        //node消息总数
    private int nodeUReadCount;        //node未读数量

    private int starTotalCount;        //star消息总数
    private int starunReadCount;        //star未读数量
    private int drafTotalCount;        //draf消息总数
    private int drafUnReadCount;        //draf未读数量
    private int sendTotalCount;        //send消息总数
    private int sendunReadCount;        //send未读数量
    private int garbageCount;          //garbage未读邮件总数
    private int garbageUnReadCount;        //garbage未读数量
    private int deleteTotalCount;        //delete消息总数
    private int deleteUnReadCount;        //delete未读数量

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getUnReadCount() {
        return unReadCount;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public int getNodeTotalCount() {
        return nodeTotalCount;
    }

    public void setNodeTotalCount(int nodeTotalCount) {
        this.nodeTotalCount = nodeTotalCount;
    }

    public int getNodeUReadCount() {
        return nodeUReadCount;
    }

    public void setNodeUReadCount(int nodeUReadCount) {
        this.nodeUReadCount = nodeUReadCount;
    }

    public int getStarTotalCount() {
        return starTotalCount;
    }

    public void setStarTotalCount(int starTotalCount) {
        this.starTotalCount = starTotalCount;
    }

    public int getStarunReadCount() {
        return starunReadCount;
    }

    public void setStarunReadCount(int starunReadCount) {
        this.starunReadCount = starunReadCount;
    }

    public int getDrafTotalCount() {
        return drafTotalCount;
    }

    public void setDrafTotalCount(int drafTotalCount) {
        this.drafTotalCount = drafTotalCount;
    }

    public int getDrafUnReadCount() {
        return drafUnReadCount;
    }

    public void setDrafUnReadCount(int drafUnReadCount) {
        this.drafUnReadCount = drafUnReadCount;
    }

    public int getSendTotalCount() {
        return sendTotalCount;
    }

    public void setSendTotalCount(int sendTotalCount) {
        this.sendTotalCount = sendTotalCount;
    }

    public int getSendunReadCount() {
        return sendunReadCount;
    }

    public void setSendunReadCount(int sendunReadCount) {
        this.sendunReadCount = sendunReadCount;
    }

    public int getGarbageCount() {
        return garbageCount;
    }

    public void setGarbageCount(int garbageCount) {
        this.garbageCount = garbageCount;
    }

    public int getGarbageUnReadCount() {
        return garbageUnReadCount;
    }

    public void setGarbageUnReadCount(int garbageUnReadCount) {
        this.garbageUnReadCount = garbageUnReadCount;
    }

    public int getDeleteTotalCount() {
        return deleteTotalCount;
    }

    public void setDeleteTotalCount(int deleteTotalCount) {
        this.deleteTotalCount = deleteTotalCount;
    }

    public int getDeleteUnReadCount() {
        return deleteUnReadCount;
    }

    public void setDeleteUnReadCount(int deleteUnReadCount) {
        this.deleteUnReadCount = deleteUnReadCount;
    }
}
