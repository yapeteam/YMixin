package cn.yapeteam.ymixin.map.impl;

import cn.yapeteam.ymixin.map.IMappingReader;
import cn.yapeteam.ymixin.utils.ASMUtils;
import cn.yapeteam.ymixin.utils.Mapper;
import cn.yapeteam.ymixin.utils.StringUtil;

import java.util.ArrayList;

public class SrgMappingReader implements IMappingReader {
    @Override
    public void readMapping(String content, ArrayList<Mapper.Map> dest) {
        content = content.replace("\r", "\n");
        dest.clear();
        for (String line : content.split("\n")) {
            line = line.replace("\n", "");
            if (line.length() <= 4) continue;
            String[] values = line.substring(4).split(" ");
            String[] obf, friendly;
            switch (line.substring(0, 2)) {
                case "CL":
                    dest.add(new Mapper.Map(null, values[1], null, values[0], Mapper.Type.Class));
                    break;
                case "FD":
                    if (values.length == 4) {
                        obf = StringUtil.split(values[0], "/");
                        friendly = StringUtil.split(values[2], "/");
                        dest.add(new Mapper.Map(
                                values[2].replace("/" + friendly[friendly.length - 1], ""),
                                friendly[friendly.length - 1],
                                values[3],
                                obf[obf.length - 1],
                                Mapper.Type.Field
                        ));
                    } else if (values.length == 2) {
                        obf = StringUtil.split(values[0], "/");
                        friendly = StringUtil.split(values[1], "/");
                        dest.add(new Mapper.Map(
                                values[1].replace("/" + friendly[friendly.length - 1], ""),
                                friendly[friendly.length - 1],
                                null,
                                obf[obf.length - 1],
                                Mapper.Type.Field
                        ));
                    }
                    break;
                case "MD":
                    obf = ASMUtils.split(values[0], "/");
                    friendly = ASMUtils.split(values[2], "/");
                    dest.add(
                            new Mapper.Map(
                                    values[2].replace("/" + friendly[friendly.length - 1], ""),
                                    friendly[friendly.length - 1],
                                    values[3],
                                    obf[obf.length - 1],
                                    Mapper.Type.Method
                            )
                    );
            }
        }
    }
}
