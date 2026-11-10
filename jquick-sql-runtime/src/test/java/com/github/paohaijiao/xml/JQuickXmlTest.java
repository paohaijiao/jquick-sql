package com.github.paohaijiao.xml;

import com.github.paohaijiao.domain.JQuickTable;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.xml.dao.DataDao;
import com.github.paohaijiao.xml.factory.JQuickFactory;
import com.github.paohaijiao.xml.factory.JQuickXmlFactory;
import com.github.paohaijiao.xml.mock.DataServiceImpl;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class JQuickXmlTest {

    @Test
    public void query() {
        DataServiceImpl service= new DataServiceImpl();
        JQuickTable table=service.getTable();
        List<JQuickTable> tables=Arrays.asList(table);
        JQuickSQLXmlParseFactory handler = new JQuickSQLXmlParseFactory(tables);
        JQuickFactory factory = new JQuickXmlFactory(handler, "jquick-sql.xml");
        DataDao dao = factory.createApi(DataDao.class);
        List<JQuickRow>  list= dao.selectData(1L,"1","2",null,new Date());
        System.out.println(list);

    }
}
