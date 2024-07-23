package cn.yapeteam.ymixin.map;

import cn.yapeteam.ymixin.utils.Mapper;

import java.util.ArrayList;

public interface IMappingReader {
    void readMapping(String content, ArrayList<Mapper.Map> dest);
}
