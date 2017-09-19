package com.centit.framework.system.dao.impl;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.jdbc.dao.BaseDaoImpl;
import com.centit.framework.jdbc.dao.DatabaseOptUtils;
import com.centit.framework.system.dao.InnerMsgRecipientDao;
import com.centit.framework.system.po.InnerMsgRecipient;
import com.centit.support.algorithm.NumberBaseOpt;
import com.centit.support.database.utils.PersistenceException;
import com.centit.support.database.utils.QueryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("innerMsgRecipientDao")
public class InnerMsgRecipientDaoImpl extends BaseDaoImpl<InnerMsgRecipient, String>
        implements InnerMsgRecipientDao {

    @Override
    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();
            filterField.put("receive", "receive = :receive");
            filterField.put("sender", "msgCode in ( select  im.MSG_CODE from M_INNERMSG im where im.SENDER = :sender )");
            filterField.put("msgContent", "msgCode in ( select  im.MSG_CODE from M_INNERMSG im where im.MSG_CONTENT LIKE :msgContent )");
            filterField.put("msgTitle", "msgCode in ( select im.MSG_CODE from M_INNERMSG im where im.MSG_TITLE LIKE :msgTitle )");
            filterField.put("mailType", "msgCode in ( select im.MSG_CODE from M_INNERMSG im where im.MAIL_TYPE = :mailType )");
            filterField.put("mailTypeNot", "msgCode in ( select im.MSG_CODE from M_INNERMSG im where im.MAIL_TYPE != :mailTypeNot )");
            filterField.put("msgStateNot", "msgState != :msgStateNot");
            filterField.put("innerMsgStateNot", "msgCode in ( select im.MSG_CODE from M_INNERMSG im where im.MSG_STATE != :innerMsgStateNot )");
            filterField.put("isRecycled", CodeBook.EQUAL_HQL_ID);
            filterField.put("MSGSTATE", CodeBook.EQUAL_HQL_ID);
            filterField.put("msgType", "msgCode in ( select im.MSG_CODE from M_INNERMSG im where im.MSG_TYPE = :msgType )");
        }
        return filterField;
    }

    @Override
    public void saveObject(InnerMsgRecipient optMethod) {
        super.saveNewObject(optMethod);
    }

    @Override
    public List<InnerMsgRecipient> listObjects(Map<String, Object> filterMap) {
        return super.listObjectsByProperties(filterMap);
    }

    @Override
    public InnerMsgRecipient getObjectById(String id) {
        return null;
    }

    /*
         * 两人间来往消息列表
         *
         */
    @Transactional
    public  List<InnerMsgRecipient> getExchangeMsgs(String sender, String receiver) {
        
        String queryString ="where( (MSG_CODE in (Select im.MSG_CODE from M_INNERMSG im where im.SENDER= ? " +
                " and (im.MAIL_TYPE='I' or im.MAIL_TYPE='O')) and RECEIVE= ?) " +
                "or (MSG_CODE in(Select  im.MSG_CODE from M_INNERMSG im where im.sender= ? " +
                " and (im.MAIL_TYPE='I' or im.MAIL_TYPE='O')) and RECEIVE= ? )) order by msgCode desc";
        List<InnerMsgRecipient> l = listObjectsByFilter(queryString,
                new Object[]{sender,receiver,receiver,sender});
        return l;
    }
   
    public long getUnreadMessageCount(String userCode){

        Object obj = DatabaseOptUtils.getScalarObjectQuery(this, "select count(1)"
                + " Where receive = ? and msgState ='U'",
                new Object[]{userCode});
        Long l = NumberBaseOpt.castObjectToLong(obj);
        return l==null?0l:l;
    }
    
    public List<InnerMsgRecipient> listUnreadMessage(String userCode){
        return listObjectsByProperties(QueryUtils.createSqlParamsMap(
                "receive", userCode,"msgState","U"));
    }
}
 