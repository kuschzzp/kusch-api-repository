package com.kusch.apis.service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 获取clashnode的Service
 *
 * @author Mr.kusch
 * @date 2023/1/15 16:14
 */
public interface ClashNodeService {

    /**
     * 获取指定类型的文件，txt、yaml
     *
     * @param type
     * @return
     */
    void getTaregtClashNodeService(String type, HttpServletRequest request, HttpServletResponse response);

}
