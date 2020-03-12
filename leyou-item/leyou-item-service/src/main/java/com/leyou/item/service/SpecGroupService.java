package com.leyou.item.service;

import com.leyou.item.pojo.SpecGroup;

import java.util.List;

public interface SpecGroupService {
    List<SpecGroup> queryGroupById(Long cid);

    void saveGroup(SpecGroup specGroup);

    void updateGroupById(SpecGroup specGroup);

    void deleteGroupById(Long gid);

    List<SpecGroup> queryGroupWithParam(Long cid);
}
