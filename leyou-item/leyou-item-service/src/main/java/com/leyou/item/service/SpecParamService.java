package com.leyou.item.service;

import com.leyou.item.pojo.SpecParam;

import java.util.List;

public interface SpecParamService {


    void insertParam(SpecParam specParam);

    void updateParamById(SpecParam specParam);

    void deleteParamById(Long id);

    List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching);
}
