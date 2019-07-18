package com.centit.framework.system.dao.hibernateimpl;

import com.centit.framework.core.dao.CodeBook;
import com.centit.framework.hibernate.dao.BaseDaoImpl;
import com.centit.framework.hibernate.dao.DatabaseOptUtils;
import com.centit.framework.system.dao.OptInfoDao;
import com.centit.framework.system.po.FVUserOptMoudleList;
import com.centit.framework.system.po.OptInfo;
import com.centit.framework.system.po.OptMethodUrlMap;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository("optInfoDao")
public class OptInfoDaoImpl extends BaseDaoImpl<OptInfo, String> implements OptInfoDao {

    public Map<String, String> getFilterField() {
        if (filterField == null) {
            filterField = new HashMap<String, String>();
            filterField.put("OPTID", CodeBook.EQUAL_HQL_ID);
            filterField.put("OPTURL", CodeBook.EQUAL_HQL_ID);
            filterField.put("OPTNAME", CodeBook.LIKE_HQL_ID);
            filterField.put("preOptId", CodeBook.EQUAL_HQL_ID);
            filterField.put("NP_TOPOPT", "(preOptId is null or preOptId='0')");
            filterField.put("optType", CodeBook.EQUAL_HQL_ID);
            filterField.put("optTypes", "optType in :optTypes");
            filterField.put("TOPOPTID", CodeBook.EQUAL_HQL_ID);
            filterField.put("ISINTOOLBAR", CodeBook.EQUAL_HQL_ID);
            filterField.put(CodeBook.ORDER_BY_HQL_ID, " preOptId, orderInd");
        }
        return filterField;
    }

    @Override
    @Transactional
    public List<OptInfo> listParentMenuFunc(){
        String hql1 = "FROM OptInfo where optUrl='...' order by orderInd ";
        List<OptInfo> preOpts = listObjects(hql1);
        return preOpts;
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<OptInfo> getMenuFuncByUserID(String userCode, String optType) {

        String hql = "FROM FVUserOptMoudleList where isintoolbar='Y' and userCode=?0 and opttype = ?1 ORDER BY orderind";
        // + " ORDER BY preoptid, formcode";
        List<FVUserOptMoudleList> ls = (List<FVUserOptMoudleList>)DatabaseOptUtils.findObjectsByHql
                (this, hql,new Object[]{userCode, optType});
        return mapOptMoudleListToOptInfo(ls);
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public List<String> listUserDataPowerByOptMethod(String userCode,String optId,String optMethod) {

        String sSqlsen = "select OPTSCOPECODES " +
                 "from F_V_USEROPTDATASCOPES " +
                 "where USERCODE = ?0 and OPTID = ?1 and OPTMETHOD = ?2";

        List<Object[]> l = (List<Object[]>) DatabaseOptUtils.findObjectsBySql
                 (this, sSqlsen,new Object[]{userCode, optId, optMethod});

        if(l==null)
             return null;
        List<String> scopeCodes = new ArrayList<String>();
         for(Object[] obj : l)
             scopeCodes.add(String.valueOf(obj[0]));
         return scopeCodes;
    }



    private List<OptInfo> mapOptMoudleListToOptInfo(List<FVUserOptMoudleList> ls ) {
        List<OptInfo> opts = new ArrayList<>(ls.size()+1);
        for (FVUserOptMoudleList opm : ls) {
            OptInfo opt = new OptInfo();
            opt.setFormCode(opm.getFormcode());
            opt.setImgIndex(opm.getImgindex());
            opt.setIsInToolbar(opm.getIsintoolbar());
            opt.setMsgNo(opm.getMsgno());
            opt.setOptType(opm.getOpttype());
            opt.setMsgPrm(opm.getMsgprm());
            opt.setOptId(opm.getOptid());
            opt.setOptName(opm.getOptname());
            opt.setOptUrl(opm.getOpturl());
            opt.setPreOptId(opm.getPreoptid());
            opt.setTopOptId(opm.getTopoptid());
            opts.add(opt);
            //System.out.print(opt.getOptType());
        }
        return opts;
    }



    @SuppressWarnings("unchecked")
    @Transactional
    public List<OptMethodUrlMap> listAllOptMethodUrlMap() {
        List<?> listObjects = DatabaseOptUtils.findObjectsByHql
                (this, "from OptMethodUrlMap");

        return (List<OptMethodUrlMap>) listObjects;
    }

    public int countChildrenSum(String optId){
        return (int)DatabaseOptUtils.getSingleIntByHql(this,
                "select count(1) as hasChildren from OptInfo where preOptId = ?0",optId);
    }

 /*   public List<OptInfo> listObjectsByCon(String condition){
        return this.listObjectsAll("From OptInfo where "+condition);
    }*/

    public List<OptInfo> listObjectByParentOptid(String optId){
        return this.listObjects("From OptInfo where preOptId = ?0", optId);
    }

    @Override
    public List<OptInfo> listMenuByTypes(String... types){
      Map<String, Object> map = new HashMap<>(2);
      if(types.length == 1){
        map.put("optType", types);
      }else {
        map.put("optTypes", types);
      }
      return listObjects(map);
    }

    @Override
    public void updateOptInfo(OptInfo optInfo){
        super.updateObject(optInfo);
    }

    @Override
    public List<OptInfo> listUserAllSubMenu(String userCode, String optType){
        String hql = "FROM FVUserOptMoudleList where userCode=?0 and opttype = ?1 ORDER BY orderind";
        // + " ORDER BY preoptid, formcode";
        List<FVUserOptMoudleList> ls = (List<FVUserOptMoudleList>) DatabaseOptUtils.findObjectsByHql
            (this, hql,new Object[]{userCode, optType});
        return mapOptMoudleListToOptInfo(ls);
    }

}
