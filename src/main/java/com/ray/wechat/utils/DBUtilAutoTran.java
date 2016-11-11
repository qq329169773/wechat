/*
package com.ray.wechat.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.cwc.transaction.TransactionThread;
import com.cwc.app.util.ConfigBean;
import com.cwc.app.util.StrUtil;

*/
/**
 * 2008.07.16
 * @author turboshop
 * 该类配合事务拦截器实现透明事务处理，加入被执行业务事件已经配置为事务管理，数据库连接从事务中的线程变量取，业务事件结束后，拦截器把事务关闭。从而实现透明事务。
 * 
 * 调用该类，必须通过代理，因为需要在AOP框架里面捕捉异常，执行回滚操作
 * 使用spring后，事务中的数据库连接由spring的事务关闭
 * 
 * 
 *//*

public class DBUtilAutoTran  implements DBUtilIFace
{
	static Logger log = Logger.getLogger("DB");
	private boolean deBugFlag = false;
    protected DataSource dataSource;									//由容器注入spring datasource
    
    */
/**
     * 初始化一个数据库管理器
     * @throws Exception
     *//*

    public DBUtilAutoTran()
            throws Exception
    {
    }

    */
/**
     * 插入记录
     * @param tablename			表名
     * @param data				数据
     * @return
     * @throws Exception
     *//*

    public boolean insert(String tablename, DBRow data)
        throws Exception
     {
        boolean ok = false;
        
        Connection conn = null;
        //数据为空返回失败
        if(data == null) return ok;
        
        ArrayList fieldNames = data.getFieldNames();
        //字段名为NULL返回失败
        if(fieldNames == null) return ok;
        
        //字段数量
        int fieldnamessize = fieldNames.size();
        
        //字段数为0返回失败
        if(fieldnamessize == 0) return ok;
        
        //组织SQL
        StringBuffer paraSql = new StringBuffer("(");					//组织参数
        StringBuffer paraValueSql = new StringBuffer("values(");		//组织值
        
        boolean started = false;
        for(int i = 0; i < fieldnamessize; i++)
        {
            String fieldName = (String)fieldNames.get(i);
            if(started)
            {
            	paraSql.append(",");
            	paraValueSql.append(",");
            }
            paraSql.append(fieldName);
            paraValueSql.append("?");
            started = true;
        }

        paraSql.append(")");
        paraValueSql.append(")");
        
        PreparedStatement pstmt = null;
        StringBuffer sql = new StringBuffer("INSERT INTO ");
        sql.append(tablename).append(" ");
        sql.append(paraSql.toString()).append(" ");
        sql.append(paraValueSql.toString());
        
        try
            {
        		//当处在事务中时，数据库连接从事务中的线程变量获得，否则从连接池获得
        		conn = DataSourceUtils.getConnection(dataSource);

				pstmt = conn.prepareStatement(sql.toString());
				
				if (deBugFlag)
				{
					log.info(sql.toString());
				}

				if(data != null)
                {
                    for(int i = 0; i < fieldnamessize; i++)
                    {
                        String fieldName = (String)fieldNames.get(i);	//字段名
                        DBObjectType dbField = data.getField(fieldName);	//字段对象

                        if(dbField != null)
                        {
                        	//判断字段类型是否为对象类型
                            if(dbField.getFieldType() == DBObjectType.OBJECT)		//对象类型
                            {
                                byte ll1l11llll11l1l1ll11ll1111l1l1l1l1[] = null;
                                ByteArrayOutputStream ll1111l11l1l1ll11ll1111l1l1l1l1 =
                                        new ByteArrayOutputStream();
                                //StreamUtil.putObjectIntoOutputStream(ll1l11ll1l1l1ll11ll1111l1l1l1l1.getFieldValue(), ll1111l11l1l1ll11ll1111l1l1l1l1);
                                ll1l11llll11l1l1ll11ll1111l1l1l1l1 = ll1111l11l1l1ll11ll1111l1l1l1l1.toByteArray();
                                try
                                {
                                    if(ll1111l11l1l1ll11ll1111l1l1l1l1 != null)
                                    {
                                        ll1111l11l1l1ll11ll1111l1l1l1l1.close();
                                    }
                                }
                                catch(IOException e){}

                                ByteArrayInputStream ll1lllll11l1l1ll11ll1111l1l1l1l1 =
                                        new ByteArrayInputStream(ll1l11llll11l1l1ll11ll1111l1l1l1l1);
                                pstmt.setBinaryStream(i + 1, ll1lllll11l1l1ll11ll1111l1l1l1l1, ll1l11llll11l1l1ll11ll1111l1l1l1l1.length);
                            }
                            else
                            {
                            	pstmt.setObject(i + 1, dbField.getFieldValue());		//设置预备声明变量
                            }
                        }
                        else
                        {
                        	pstmt.setObject(i, null);
                        }
                    }

                }
                pstmt.executeUpdate();
               
                ok = true;
            }
            catch(SQLException e1)
            {
            	log.error("DBUtilAutoTran.insert error:" + e1);
            	log.error("Error SQL:"+sql.toString());
                throw new SQLException("DBUtilAutoTran.insert error:" + e1);
            }
            finally
			{
					closeConn(null, pstmt, conn);
			}
            
            
            
    		//把修改过的表记录在事务中 
    		if (TransactionThread.isInTransaction())
    		{
    			TransactionThread.addTransactionUpdateTable(tablename);			
    		}

        return ok;
     }
    
    public long insertReturnId(String tablename, DBRow data)
    	throws Exception
	 {
	    long id = 0;
	    
	    Connection conn = null;
	    //数据为空返回失败
	    if(data == null) return 0;
	    
	    ArrayList fieldNames = data.getFieldNames();
	    //字段名为NULL返回失败
	    if(fieldNames == null) return 0;
	    
	    //字段数量
	    int fieldnamessize = fieldNames.size();
	    
	    //字段数为0返回失败
	    if(fieldnamessize == 0) return 0;
	    
	    //组织SQL
	    StringBuffer paraSql = new StringBuffer("(");					//组织参数
	    StringBuffer paraValueSql = new StringBuffer("values(");		//组织值
	    
	    boolean started = false;
	    for(int i = 0; i < fieldnamessize; i++)
	    {
	        String fieldName = (String)fieldNames.get(i);
	        if(started)
	        {
	        	paraSql.append(",");
	        	paraValueSql.append(",");
	        }
	        paraSql.append(fieldName);
	        paraValueSql.append("?");
	        started = true;
	    }
	
	    paraSql.append(")");
	    paraValueSql.append(")");
	    
	    PreparedStatement pstmt = null;
	    StringBuffer sql = new StringBuffer("INSERT INTO ");
	    sql.append(tablename).append(" ");
	    sql.append(paraSql.toString()).append(" ");
	    sql.append(paraValueSql.toString());
	    
	    try
	        {
	    		//当处在事务中时，数据库连接从事务中的线程变量获得，否则从连接池获得
	    		conn = DataSourceUtils.getConnection(dataSource);
	
				pstmt = conn.prepareStatement(sql.toString(),pstmt.RETURN_GENERATED_KEYS);
				
				if (deBugFlag)
				{
					log.info(sql.toString());
				}
	
				if(data != null)
	            {
	                for(int i = 0; i < fieldnamessize; i++)
	                {
	                    String fieldName = (String)fieldNames.get(i);	//字段名
	                    DBObjectType dbField = data.getField(fieldName);	//字段对象
	
	                    if(dbField != null)
	                    {
	                    	//判断字段类型是否为对象类型
	                        if(dbField.getFieldType() == DBObjectType.OBJECT)		//对象类型
	                        {
	                            byte ll1l11llll11l1l1ll11ll1111l1l1l1l1[] = null;
	                            ByteArrayOutputStream ll1111l11l1l1ll11ll1111l1l1l1l1 =
	                                    new ByteArrayOutputStream();
	                            //StreamUtil.putObjectIntoOutputStream(ll1l11ll1l1l1ll11ll1111l1l1l1l1.getFieldValue(), ll1111l11l1l1ll11ll1111l1l1l1l1);
	                            ll1l11llll11l1l1ll11ll1111l1l1l1l1 = ll1111l11l1l1ll11ll1111l1l1l1l1.toByteArray();
	                            try
	                            {
	                                if(ll1111l11l1l1ll11ll1111l1l1l1l1 != null)
	                                {
	                                    ll1111l11l1l1ll11ll1111l1l1l1l1.close();
	                                }
	                            }
	                            catch(IOException e){}
	
	                            ByteArrayInputStream ll1lllll11l1l1ll11ll1111l1l1l1l1 =
	                                    new ByteArrayInputStream(ll1l11llll11l1l1ll11ll1111l1l1l1l1);
	                            pstmt.setBinaryStream(i + 1, ll1lllll11l1l1ll11ll1111l1l1l1l1, ll1l11llll11l1l1ll11ll1111l1l1l1l1.length);
	                        }
	                        else
	                        {
	                        	pstmt.setObject(i + 1, dbField.getFieldValue());		//设置预备声明变量
	                        }
	                    }
	                    else
	                    {
	                    	pstmt.setObject(i, null);
	                    }
	                }
	
	            }
				
	            pstmt.executeUpdate();
	            ResultSet rs = pstmt.getGeneratedKeys();
	            while (rs.next()) 
	            {
					id = rs.getLong(1);
				}
	        }
	        catch(SQLException e1)
	        {
	        	log.error("DBUtilAutoTran.insert error:" + e1);
	        	log.error("Error SQL:"+sql.toString());
	            throw new SQLException("DBUtilAutoTran.insert error:" + e1);
	        }
	        finally
			{
					closeConn(null, pstmt, conn);
			}
	        
	        
	        
			//把修改过的表记录在事务中 
			if (TransactionThread.isInTransaction())
			{
				TransactionThread.addTransactionUpdateTable(tablename);			
			}
	
	    return id;
	 }
     

    */
/**
     * 更新表
     * @param wherecond			条件语句
     * @param tablename			表名
     * @param data				更新的数据
     * @return
     * @throws Exception
     *//*

    public int update(String wherecond, String tablename, DBRow data)
	    throws Exception
	{
		int c = update( wherecond, tablename, data,true);
	    return c;
	}
    
    */
/**
     * 预备声明方式更新数据
     * @param wherecond
     * @param para
     * @param tablename
     * @param data
     * @return
     * @throws Exception
     *//*

    public int updatePre(String wherecond,DBRow para, String tablename, DBRow data)
	    throws Exception
	{
		int c = update( wherecond, tablename, data,true);
	    return c;
	}
    
    */
/**
     * 预备声明方式更新数据
     * @param wherecond
     * @param para
     * @param tablename
     * @param data
     * @param clean
     * @return
     * @throws Exception
     *//*

    public int updatePre(String wherecond, DBRow para,String tablename, DBRow data,boolean clean)
	    throws Exception
	{
	    return(0);
	}
    
    */
/**
     * 更新表
     * @param wherecond			条件语句
     * @param tablename			表名
     * @param data				更新的数据
     * @param clean				是否立即清除缓存（true-立即）
     * @return
     * @throws Exception
     *//*

    public int update(String wherecond, String tablename, DBRow data,boolean clean)
        throws Exception
    {
    	Connection conn = null;
    	
        int lines = 0;		//影响记录数
        if(data == null)       return lines;
        
        //组织SQL
        StringBuffer sql = new StringBuffer();
        sql.append("update ");
        sql.append(tablename);
        sql.append(" set ");
        
        //获得所有需要更新的字段名
        ArrayList fieldNames = data.getFieldNames();
        
        if(fieldNames == null || fieldNames.size() == 0) return 0;
        
        //字段数
        int size = fieldNames.size();
        
        //初始化一个字段数组
        DBObjectType dbfields[] = new DBObjectType[size];
        
        //存放字段名
        ArrayList fieldNameAl = new ArrayList();
        
        for(int i = 0; i < size; i++)
        {
            String fieldName = (String)fieldNames.get(i);
            DBObjectType dbField = data.getField(fieldName);	//字段对象
            
            if(dbField != null)
            {
            	dbfields[i] = dbField;
                Object ll1111ll11l1ll11ll1l1l1lllll11l11l1l1l1l11 = dbField.getFieldValue();	//字段值
                sql.append(fieldName);
                sql.append("=");
                
                //组织预备声明
                if(ll1111ll11l1ll11ll1l1l1lllll11l11l1l1l1l11 == null)
                {
                	sql.append("null ");
                } else
                {
                	sql.append("? ");
                	fieldNameAl.add(fieldName);
                }
                if(i < size - 1) sql.append(", ");
            }
        }

        sql.append(wherecond);	//附加条件语句

        PreparedStatement pstmt = null;
        size = fieldNameAl.size();		//需要插入的字段数
        String fieldNamesA[];
        
        if(size > 0)
        {
        	fieldNamesA = (String[])fieldNameAl.toArray(new String[0]);			//把需要更新的字段名转换成数组
        }
        else
        {
        	fieldNamesA = new String[0];
        }

        try
        {
			conn = DataSourceUtils.getConnection(dataSource);
			pstmt = conn.prepareStatement(sql.toString());
			if (deBugFlag)
			{
				log.info(sql.toString());
			}
			
            for(int i = 0; i < size; i++)
            {
                String tmp = fieldNamesA[i];
                DBObjectType dbField = data.getField(tmp);
                Object fieldValue = dbField.getFieldValue();
                
                if(dbField.getFieldType() == DBObjectType.OBJECT)		//字段值对象类型
                {
                    byte ll1l11llll11l1l1ll11ll1111l1l1l1l1[] = null;
                    ByteArrayOutputStream ll1111l11l1l1ll11ll1111l1l1l1l1 = new ByteArrayOutputStream();
                    //StreamUtil.putObjectIntoOutputStream(ll1111ll11l1ll11ll1l1l1lllll11l11l1l1l1l11, ll1111l11l1l1ll11ll1111l1l1l1l1);
                    ll1l11llll11l1l1ll11ll1111l1l1l1l1 = ll1111l11l1l1ll11ll1111l1l1l1l1.toByteArray();
                    try
                    {
                        if(ll1111l11l1l1ll11ll1111l1l1l1l1 != null)
                        {
                            ll1111l11l1l1ll11ll1111l1l1l1l1.close();
                        }
                    }
                    catch(IOException e){}
                    ByteArrayInputStream ll1lllll11l1l1ll11ll1111l1l1l1l1 = new ByteArrayInputStream(ll1l11llll11l1l1ll11ll1111l1l1l1l1);
                    pstmt.setBinaryStream(i + 1, ll1lllll11l1l1ll11ll1111l1l1l1l1, ll1l11llll11l1l1ll11ll1111l1l1l1l1.length);
                }
                else
                {
                	pstmt.setObject(i + 1, fieldValue);
                }
            }
            lines = pstmt.executeUpdate();
            
        }
        catch(SQLException e)
        {
        	log.error("DBUtilAutoTran.update error:" + e);
        	log.error("DBUtilAutoTran.update errorSql:" + sql.toString());
            throw new SQLException("DBUtilAutoTran.update error:" + e);
        }
        finally
		{  
				 closeConn(null, pstmt, conn);    
		}

		
		
		//把修改过的表记录在事务中 
		if (TransactionThread.isInTransaction())
		{
			TransactionThread.addTransactionUpdateTable(tablename);			
		}
        
        return lines;

    } 
    
    */
/**
     * 删除记录
     * @param wherecond			条件语句
     * @param tablename			表名
     * @return
     * @throws Exception
     *//*

    public int delete(String wherecond, String tablename)
        throws Exception
    {
    	Connection conn = null;
    	
        int num = 0;
        String sql =null;
        
		try
		{
			if(tablename == null)
			{
			    return 0;
			}
			sql = "delete from " + tablename + " " + (wherecond==null?"":wherecond);
			
			if (deBugFlag)
			{
				log.info(sql.toString());
			}

			
			conn = DataSourceUtils.getConnection(dataSource);
			
			num = conn.createStatement().executeUpdate(sql);
		} 
		catch (SQLException e)
		{
			log.error("DBUtilAutoTran.delete error:" + e);
			log.error("errorSql:" + sql);
			throw new SQLException("delete error:" + e);
		}
        finally
		{
            
				closeConn(null, null, conn);         
		}        

        
        
		//把修改过的表记录在事务中 
		if (TransactionThread.isInTransaction())
		{
			TransactionThread.addTransactionUpdateTable(tablename);			
		}
        
        return num;
    }
    
    */
/**
     * 预备声明方式删除
     * @param wherecond
     * @param tablename
     * @param para
     * @return
     * @throws Exception
     *//*

    public int deletePre(String wherecond,DBRow para, String tablename)
	    throws Exception
	{
		Connection conn = null;
		
	    int num = 0;
	    String sql =null;
	    PreparedStatement pstmt;
	    
		try
		{
			if(tablename == null)
			{
			    return 0;
			}
			sql = "delete from " + tablename + " " + (wherecond==null?"":wherecond);
			
			if (deBugFlag)
			{
				log.info(sql.toString());
			}
			
			conn = DataSourceUtils.getConnection(dataSource);
			pstmt = conn.prepareStatement(sql);
			
			ArrayList fieldList = para.getFieldNames();
			for (int i=0; i<fieldList.size(); i++)
			{
				//区分数据类型，全部用字符串获得
				pstmt.setObject(i+1, para.getString( fieldList.get(i).toString() ));
			}
			
			num = pstmt.executeUpdate();
		} 
		catch (SQLException e)
		{
			e.printStackTrace();
			log.error("DBUtilAutoTran.deletePre error:" + e);
			log.error("errorSql:" + sql);
			//把参数打印一下
			for (int i=0; i<para.getFieldNames().size(); i++)
			{
//				System.out.println(para.getFieldNames().get(i)+" : "+para.getString( para.getFieldNames().get(i).toString() ));
			}
			
			throw new SQLException("deletePre error:" + e);
		}
	    finally
		{
	        
				closeConn(null, null, conn);         
		}        
	
	    
	    
		//把修改过的表记录在事务中 
		if (TransactionThread.isInTransaction())
		{
			TransactionThread.addTransactionUpdateTable(tablename);			
		}
	    
	    return num;
	}
    
    

    */
/**
     * 把检索结果转换为DBROW并计算翻页
     * @param rs				检索结果
     * @param pageCtrl			翻页类
     * @return
     * @throws Exception
     *//*

    private DBRow[] getResultsMap(ResultSet rs, PageCtrl pageCtrl)
          throws Exception
      {
    	
          int allCount = 0;			//记录总数
          int start = 0;			//开始偏移
          int end = 0;				//结束便宜
          int ind = 0;				//游标偏移
          int pageSize = 0;			//页面记录数
          int pageNo = 0;			//当前页
          
          if(pageCtrl != null)
          {
        	  pageSize = pageCtrl.getPageSize();		//得到外部设置的每页显示记录数
        	  pageNo = pageCtrl.getPageNo();			//得到外部设置当前第几页
        	  
             if(pageSize <= 0 || pageNo < 1) throw new Exception("PageCtrl data is error");
             
              start = ( pageNo - 1) * pageSize;			//计算起始偏移量
              end = pageNo * pageSize;					//计算终点偏移量
          }
          else
          {
              //debug: System.out.println("PageCtrl is null");
          }

          int cols_size = 0;
          DBRow results[] = null;
          DBObjectType dbFieldOut[] = getFieldInfos(rs);		//把RS转换为字段对象
          
          if(dbFieldOut != null) cols_size = dbFieldOut.length;			//字段数
          ArrayList list = new ArrayList();

          while(rs != null && rs.next())
          {
        	  //记录结果集总数
        	  allCount++;
        	  
              if( (++ind > start && ind <= end ) || (start == 0 && end == 0) )		//跳过偏移量外的结果集
              {
                  DBRow data = new DBRow();
                  
                  //封装一行结果集
                  for(int i = 0; i < cols_size; i++)
                  {
                      Object obj = null;
                      DBObjectType df = dbFieldOut[i];
                      
                      if(df != null)
                      {
                          String fieldName = df.getFieldName();
                          DBObjectType dbField = (DBObjectType)df.clone();			//克隆一份dbfield，相当于NEW一个DBFIELD并复制数据
//                        dbField.setFieldValue(rs.getObject(dbField.getFieldName()));

                          try
                          {
                              if(dbField.getFieldType() == DBObjectType.OBJECT)			//对象类型
                              {
                              	  String tmpStr;
                              	  StringBuffer sb = new StringBuffer(); 
                                  
                              	  Clob clob = rs.getClob(fieldName);
                              	  Reader iis = clob.getCharacterStream();
                                  BufferedReader brr = new BufferedReader(iis);
                                  while ( (tmpStr=brr.readLine())!=null )
                        		  {
                                  	sb.append(tmpStr);
                      			  }
                                  dbField.setFieldValue( sb.toString());
                              } 
                              else
                              {
                            	  obj = rs.getObject(fieldName);
                            	  dbField.setFieldValue(obj);				//对dbfield进行赋值（因为从getFieldInfos获得DBFIELD只设置了字段名和其字段类型，并没有赋值）

                              }
                          }
                          catch(Exception e)
                          {
                          	 log.error("DBUtilAutoTran.getResultsMap read from " + fieldName + "  error"+e);
                          }
                          
                          if(dbField != null && dbField.getFieldValue() != null) 
                          {
                        	  data.addField(dbField);			//把dbfield付给输出结果data  
                          }
                          
                      }
                  }

                  list.add(data);	//把一行记录加入arraylist，在最后转换为DBRow[]
              }
          }
          
          
          if(list.size() > 0)
          {
        	  results = (DBRow[])list.toArray(new DBRow[0]);
          }
          else
          {
        	  results = new DBRow[0];
          }
          
          //计算分页结果
          if(pageCtrl != null)
          {
        	  //计算总页数
              int pageCount = ((allCount + pageSize) - 1) / pageSize;

              if( pageCount < pageNo)
              {
            	  pageNo = pageCount;
              }

              pageCtrl.setPageNo(pageNo);				//当前页
              pageCtrl.setPageSize(pageSize);			//每页记录数
              pageCtrl.setPageCount(pageCount);			//总页数
              pageCtrl.setAllCount(allCount);			//总记录数
              pageCtrl.setRowCount(results.length);		//当前页记录数
          }
          
          return results;
      }

//    */
/**
//     * 单返回结果集查询（带缓存）
//     * @param sql
//     * @param tablenames
//     * @return
//     * @throws Exception
//     *//*

//    public DBRow selectSingleCache(String sql,String tablenames[])
//	    throws Exception
//	{
//		String sqlKey = makeSqlKey(sql,null,null);	//sql key
//		
//		
//		try 
//		{
//			DBRow data;
//			//如果查询的表在事务中被更新过或者内存中没有记录，都需要从数据库查询
//			//在事务中更新的表，在提交前是临时性数据，而表的内存更新标志是在事务提交后才刷新
//			//所以在事务中被更新的表
//			if ( TransactionThread.isUpdated(tablenames)||(data=(DBRow)cacheMgr.getObject(sqlKey))==null )
//			{
//				data=selectSingle(sql);
//				
//				if (!TransactionThread.isUpdated(tablenames))
//				{
//					cacheMgr.putObject(tablenames,sqlKey,data);					
//				}
//			}
//	   		return(data);
//		}
//		catch (Exception e) 
//		{
//			throw new Exception("DBUtil selectSingleCache(sql) error:"+e);
//		}
//	}

    */
/**
     * 单返回结果集查询
     * @param sql
     * @return
     * @throws SQLException
     *//*

    public DBRow selectSingle(String sql)
        throws SQLException
    {
    	//通过多结果集查询
        DBRow rows[] = selectMutliple(sql, null);
        
        //结果集大于1不符合要求
        if(rows == null || rows.length == 0) return(null);
        
        if(rows.length > 1)
        {
        	log.error("DBUtilAutoTran.selectSingle error:Select Multiple data but only one is ok");
        	log.error("Error SQL:"+sql);
        	throw new SQLException("DBUtilAutoTran.selectSingle error:Select Multiple data but only one is ok");
        }           
        else
        {
            return rows[0];        	
        }
    }
    
//    */
/**
//     * 使用预备声明单结果集查询（带缓存）
//     * @param sql								
//     * @param para					参数
//     * @param tablenames			表
//     * @return
//     * @throws Exception
//     *//*

//    public DBRow selectPreSingleCache(String sql,DBRow para,String tablenames[])
//	    throws Exception
//	{
//		String sqlKey = makeSqlKey(sql,para,null);
//		
//		try 
//		{
//			DBRow data;
//			if ( TransactionThread.isUpdated(tablenames)||(data=(DBRow)cacheMgr.getObject(sqlKey))==null )
//			{
//				data=selectPreSingle(sql,para);
//				
//				if (!TransactionThread.isUpdated(tablenames))
//				{
//					cacheMgr.putObject(tablenames,sqlKey,data);					
//				}
//			}
//	   		return(data);
//		}
//		catch (Exception e) 
//		{
//			throw new Exception("DBUtil selectPreSingleCache(sql,ll1ll11ll11111l1l111l1l1111ll111111) error:"+e);
//		}
//	}

    */
/**
     * 使用预备声明单结果集查询
     * @param sql
     * @param para
     * @return
     * @throws SQLException
     *//*

    public DBRow selectPreSingle(String sql,DBRow para)
	    throws SQLException
	{
	    DBRow rows[] = selectPreMutliple(sql,para,null);
	    
	    if(rows == null || rows.length == 0) return(null);
	    
	    //结果集不符合要求
	    if(rows.length > 1)
	    {
	    	log.error("DBUtilAutoTran.selectSingle(prepare) error:Select Multiple data but only one is ok");
	    	log.error("Error SQL:"+sql);
	    	ArrayList paraAL = para.getFieldNames();
	    	for (int i=0; i<paraAL.size(); i++)
	    	{
	    		log.error(paraAL.get(i)+"="+para.getString(paraAL.get(i).toString()));
	    	}
	    	throw new SQLException("DBUtilAutoTran.selectSingle(prepare) error:Select Multiple data but only one is ok");
	    }           
	    else
	    {
	        return rows[0];        	
	    }
	}

//    */
/**
//     * 使用预备声明多结果集查询（带缓存）
//     * @param sql
//     * @param para
//     * @param tablenames
//     * @return
//     * @throws Exception
//     *//*

//    public DBRow[] selectPreMutlipleCache(String sql,DBRow para,String tablenames[])
//	    throws Exception
//	{
//    	String sqlkey = makeSqlKey(sql,para,null);
//    	
//    	
//		try 
//		{
//			DBRow data[];
//			
//			if ( TransactionThread.isUpdated(tablenames)||(data=(DBRow[])cacheMgr.getObject(sqlkey))==null )
//			{
//				data=selectPreMutliple(sql,para);
//				
//				if (!TransactionThread.isUpdated(tablenames))
//				{
//					cacheMgr.putObject(tablenames,sqlkey,data);					
//				}
//			}
//	   		return(data);
//		}
//		catch (Exception e) 
//		{
//			throw new Exception("DBUtil selectPreMutlipleCache(sql,ll1ll11ll11111l1l111l1l1111ll111111) error:"+e);
//		}
//	}
    
    */
/**
     * 使用预备声明多结果集查询
     * @param sql
     * @param para
     * @return
     * @throws SQLException
     *//*

    public DBRow[] selectPreMutliple(String sql,DBRow para)
	    throws SQLException
	{
	    return selectPreMutliple(sql,para,null);
	}
    
//    */
/**
//     * 使用预备声明多结果集带翻页查询（带缓存）
//     * @param sql
//     * @param para
//     * @param pc
//     * @param tablenames
//     * @return
//     * @throws Exception
//     *//*

//    public DBRow[] selectPreMutlipleCache(String sql,DBRow para,PageCtrl pc,String tablenames[])
//	    throws Exception
//	{
//    	//翻页为NULL，则不返回所有结果集
//    	if (pc==null)
//    	{
//    		return( selectPreMutlipleCache( sql, para, tablenames) );
//    	}
//    	
//    	String sqlkey = makeSqlKey(sql,para,pc);
//    	
//
//		try
//		{
//			DBRow data[]=new DBRow[0];
//			
//			//在事务中这些表没有被更新过并且内存记录不为空，则从内存中获取数据
//			if ( !TransactionThread.isUpdated(tablenames)&&cacheMgr.getObject(sqlkey)!=null )
//			{
//				//如果缓存中有数据，则把结果集合翻页取出来
//				ArrayList result = (ArrayList)cacheMgr.getObject(sqlkey);
//				data = (DBRow[])result.get(0);
//
//				PageCtrl oldPc = (PageCtrl)result.get(1);
//				pc.setAllCount(oldPc.getAllCount());
//				pc.setPageCount(oldPc.getPageCount());
//				pc.setPageNo(oldPc.getPageNo());
//				pc.setPageSize(oldPc.getPageSize());
//				pc.setRowCount(oldPc.getRowCount());
//			}
//			else
//			{
//				data=selectPreMutliple(sql,para,pc);
//
//				if (!TransactionThread.isUpdated(tablenames))
//				{
//					//把结果集合翻页数据保存
//					ArrayList newResult = new ArrayList();
//					newResult.add(data);
//					newResult.add(pc);
//					cacheMgr.putObject(tablenames,sqlkey,newResult);					
//				}
//			}
//	   		return(data);
//		}
//		catch (Exception e) 
//		{
//			throw new Exception("DBUtil selectPreMutlipleCache(sql,ll1ll11ll11111l1l111l1l1111ll111111,pc) error:"+e);
//		}
//	}

    */
/**
     * 使用预备声明多结果集带翻页查询
     * @param sql
     * @param para
     * @param pc
     * @return
     * @throws SQLException
     *//*

    public DBRow[] selectPreMutliple(String sql,DBRow para,PageCtrl pc)
	    throws SQLException
	{
    	Connection conn = null;
    	
    	//如果参数为空，则返回NULL
    	ArrayList fieldNames = para.getFieldNames();
    	if ( fieldNames==null||fieldNames.size()==0 )
    	{
    		return(null);
    	}
    	
    	int fieldnamessize = fieldNames.size();
    	
    	
	    DBRow results[] = null;
	    if( (sql==null) || (sql.length() == 0) )
	    {
	    	return(null);
	    }	        
	

	        PreparedStatement pstmt = null;
	        ResultSet rs = null;
	        
	        conn = DataSourceUtils.getConnection(dataSource);

	        try
	        {
	        	pstmt = conn.prepareStatement(sql);
				if (deBugFlag)
				{
					log.info(sql.toString());
				}

	        	for(int i = 0; i < fieldnamessize; i++)
	        	{
                    String fieldname = (String)fieldNames.get(i);
                    DBObjectType dbField = para.getField(fieldname);
                    
                    if(dbField != null)
                    {
                        if(dbField.getFieldType() == DBObjectType.OBJECT)
                        {
//                            byte ll1l11llll11l1l1ll11ll1111l1l1l1l1[] = null;
//                            ByteArrayOutputStream ll1111l11l1l1ll11ll1111l1l1l1l1 = new ByteArrayOutputStream();
//                            ll1l11llll11l1l1ll11ll1111l1l1l1l1 = ll1111l11l1l1ll11ll1111l1l1l1l1.toByteArray();
//                            try
//                            {
//                                if(ll1111l11l1l1ll11ll1111l1l1l1l1 != null)
//                                {
//                                    ll1111l11l1l1ll11ll1111l1l1l1l1.close();
//                                }
//                            }
//                            catch(IOException e){}
//
//                            ByteArrayInputStream ll1lllll11l1l1ll11ll1111l1l1l1l1 = new ByteArrayInputStream(ll1l11llll11l1l1ll11ll1111l1l1l1l1);
//                            ll1l11llll1l1l1ll11ll11l11l1l1l1l1.setBinaryStream(ll1l1llll11l1l1ll11ll1111l1l1l1l1 + 1, ll1lllll11l1l1ll11ll1111l1l1l1l1, ll1l11llll11l1l1ll11ll1111l1l1l1l1.length);
                        	log.error("DBUtilAutoTran.selectPreMutliple(ll1ll11ll11111l1l111l1l1111ll111111) error:ll1l11ll1l1l1ll11ll1111l1l1l1l1.getFieldType()=4!");
                        }
                        else
                        {
							//ll1l11llll1l1l1ll11ll11l11l1l1l1l1.setObject(ll1l1llll11l1l1ll11ll1111l1l1l1l1 + 1,ll1l11ll1l1l1ll11ll1111l1l1l1l1.getFieldValue());
                        	pstmt.setObject(i + 1,para.getString(fieldname));
                        }
                    }
                    else
                    {
//                        ll1l11llll1l1l1ll11ll11l11l1l1l1l1.setObject(ll1l1llll11l1l1ll11ll1111l1l1l1l1, null);
                    	log.error("DBUtilAutoTran.selectPreMutliple(ll1ll11ll11111l1l111l1l1111ll111111) error:ll1l11ll1l1l1ll11ll1111l1l1l1l1=null!");
                    }
	        	}        	
	        	rs = pstmt.executeQuery();
	        	//把搜索接股票转换为DBROW
	            results = getResultsMap(rs, pc);
	        }
	        catch(Exception e1)
	        {
		    	log.error("DBUtilAutoTran.selectMutliple(prepare) error:" + e1);
		    	log.error("Error SQL:"+sql);
		    	
		    	ArrayList paraAL = para.getFieldNames();
		    	for (int i=0; i<paraAL.size(); i++)
		    	{
		    		log.error(paraAL.get(i)+"="+para.getString(paraAL.get(i).toString()));
		    	}
		    	
		        throw new SQLException("DBUtilAutoTran.selectMutliple(prepare) error:" + e1);
	        }
	        finally
			{
					closeConn(rs, pstmt, conn);   
			}

	    
	    return results;
	}
    
//    */
/**
//     * 多结果集查询（带缓存）
//     * @param sql
//     * @param tablenames
//     * @return
//     * @throws Exception
//     *//*

//    public DBRow[] selectMutlipleCache(String sql,String tablenames[])
//	    throws Exception
//	{
//    	String sqlkey = makeSqlKey(sql,null,null);
//    	
//    	
//		try 
//		{
//			DBRow data[];
//			
//			if ( TransactionThread.isUpdated(tablenames)||(data=(DBRow[])cacheMgr.getObject(sqlkey))==null )
//			{
//				data=selectMutliple(sql);
//				
//				if (!TransactionThread.isUpdated(tablenames))
//				{
//					cacheMgr.putObject(tablenames,sqlkey,data);					
//				}
//			}			
//	   		return(data);
//		}
//		catch (Exception e) 
//		{
//			throw new Exception("DBUtil selectMutlipleCache(sql) error:"+e);
//		}
//	}

    */
/**
     * 多结果集查询
     * @param sql
     * @return
     * @throws SQLException
     *//*

    public DBRow[] selectMutliple(String sql)
        throws SQLException
    {
        return selectMutliple(sql, null);
    }

//    */
/**
//     * 多结果集查询（带缓存）
//     * @param sql
//     * @param pc
//     * @param tablenams
//     * @return
//     * @throws Exception
//     *//*

//    public DBRow[] selectMutlipleCache(String sql,PageCtrl pc,String tablenams[])
//	    throws Exception
//	{
//    	String sqlkey = makeSqlKey(sql,null,pc);
//    	
//    	
//		try
//		{
//			DBRow data[]=new DBRow[0];
//			
//			if ( !TransactionThread.isUpdated(tablenams)&&cacheMgr.getObject(sqlkey)!=null )
//			{
//				//存缓存中获得结果集数据和翻页数据
//				ArrayList result = (ArrayList)cacheMgr.getObject(sqlkey);
//				data = (DBRow[])result.get(0);
//
//				PageCtrl oldPc = (PageCtrl)result.get(1);
//				pc.setAllCount(oldPc.getAllCount());
//				pc.setPageCount(oldPc.getPageCount());
//				pc.setPageNo(oldPc.getPageNo());
//				pc.setPageSize(oldPc.getPageSize());
//				pc.setRowCount(oldPc.getRowCount());
//			}
//			else
//			{
//				data=selectMutliple(sql,pc);
//				
//				if (!TransactionThread.isUpdated(tablenams))
//				{
//					ArrayList newResult = new ArrayList();
//					newResult.add(data);
//					newResult.add(pc);
//					cacheMgr.putObject(tablenams,sqlkey,newResult);
//				}
//			}		
//	   		return(data);
//		}
//		catch (Exception e) 
//		{
//			throw new Exception("DBUtil selectMutlipleCache(sql,pc) error:"+e);
//		}
//	}
    
//    */
/**
//     * 获得一定数量结果集查询（带缓存）
//     * @param sql
//     * @param count
//     * @param tablenames
//     * @return
//     * @throws Exception
//     *//*

//    public DBRow[] selectMutlipleCacheByCount(String sql,int count,String tablenames[])
//	    throws Exception
//	{
//		PageCtrl pc = new PageCtrl();
//		pc.setPageNo(1);
//		pc.setPageSize(count);
//		
//		String sqlkey = makeSqlKey(sql+"|count="+String.valueOf(count),null,pc);
//		
//		
//		try
//		{
//			DBRow data[]=new DBRow[0];
//			
//			if ( !TransactionThread.isUpdated(tablenames)&&cacheMgr.getObject(sqlkey)!=null )
//			{
//				ArrayList result = (ArrayList)cacheMgr.getObject(sqlkey);
//				data = (DBRow[])result.get(0);
//	
//				PageCtrl oldPc = (PageCtrl)result.get(1);
//				pc.setAllCount(oldPc.getAllCount());
//				pc.setPageCount(oldPc.getPageCount());
//				pc.setPageNo(oldPc.getPageNo());
//				pc.setPageSize(oldPc.getPageSize());
//				pc.setRowCount(oldPc.getRowCount());
//			}
//			else
//			{
//				data=selectMutliple(sql,pc);
//				
//				if (!TransactionThread.isUpdated(tablenames))
//				{
//					ArrayList newPc = new ArrayList();
//					newPc.add(data);
//					newPc.add(pc);
//					cacheMgr.putObject(tablenames,sqlkey,newPc);
//				}
//			}		
//	   		return(data);
//		}
//		catch (Exception e) 
//		{
//			throw new Exception("DBUtil selectMutlipleCacheByCount(sql,pc) error:"+e);
//		}
//	}
	
//    */
/**
//     * 使用预备声明获得一定结果集查询（带缓存）
//     * @param sql
//     * @param para
//     * @param count
//     * @param tablenames
//     * @return
//     * @throws Exception
//     *//*

//	public DBRow[] selectPreMutlipleCacheByCount(String sql,DBRow para,int count,String tablenames[])
//	    throws Exception
//	{
//		PageCtrl pc = new PageCtrl();
//		pc.setPageNo(1);
//		pc.setPageSize(count);
//		
//		String sqlkey = makeSqlKey(sql+"|count="+String.valueOf(count),para,pc);
//		
//		
//		try
//		{
//			DBRow data[]=new DBRow[0];
//			
//			if ( !TransactionThread.isUpdated(tablenames)&&cacheMgr.getObject(sqlkey)!=null )
//			{
//				ArrayList result = (ArrayList)cacheMgr.getObject(sqlkey);
//				data = (DBRow[])result.get(0);
//	
//				PageCtrl oldPc = (PageCtrl)result.get(1);
//				pc.setAllCount(oldPc.getAllCount());
//				pc.setPageCount(oldPc.getPageCount());
//				pc.setPageNo(oldPc.getPageNo());
//				pc.setPageSize(oldPc.getPageSize());
//				pc.setRowCount(oldPc.getRowCount());
//			}
//			else
//			{
//				data=selectPreMutliple(sql,para,pc);
//				
//				if (!TransactionThread.isUpdated(tablenames))
//				{
//					ArrayList newPc = new ArrayList();
//					newPc.add(data);
//					newPc.add(pc);
//					cacheMgr.putObject(tablenames,sqlkey,newPc);
//				}
//			}		
//	   		return(data);
//		}
//		catch (Exception e) 
//		{
//			throw new Exception("DBUtil selectPreMutlipleCacheByCount(sql,ll1ll11ll11111l1l111l1l1111ll111111,pc) error:"+e);
//		}
//	}
    
	*/
/**
	 * 带翻页多结果集查询
	 * @param sql
	 * @param pc
	 * @return
	 * @throws SQLException
	 *//*

	public DBRow[] selectMutliple(String sql, PageCtrl pc)
        throws SQLException
    {
		Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
		
        DBRow data[] = null;
        if( (sql==null) || (sql.length() == 0) ) return null;

        try
        {
            conn = DataSourceUtils.getConnection(dataSource);
            
			if (deBugFlag)
			{
				log.info(sql.toString());
			}
           	pstmt = conn.prepareStatement(sql);        
           	rs = pstmt.executeQuery();
           	//把查询结果转换为DBROW
            data = getResultsMap(rs, pc);
        }
        catch(Exception e)
        {
        	log.error("DBUtilAutoTran.selectMutliple error:" + e);
        	log.error("Error SQL:"+sql);
            throw new SQLException("DBUtilAutoTran.selectMutliple error:" + e);
        }
        finally
		{
				closeConn(rs, pstmt, conn);      
		}
        
        return data;
    }

	*/
/**
	 * 使用预备声明获得一定结果集查询
	 * @param sql
	 * @param para
	 * @param count
	 * @return
	 * @throws SQLException
	 *//*

    public DBRow[] selectPreMutlipleByCount(String sql,DBRow para,int count)
	    throws SQLException
	{
	    try
	    {
	    	PageCtrl pc = new PageCtrl();
	    	pc.setPageNo(1);
	    	pc.setPageSize(count);
	    	return(selectPreMutliple(sql,para,pc));
	    }
	    catch(Exception e)
	    {
	    	log.error("DBUtilAutoTran.selectPreMutlipleByCount error:" + e);
	    	log.error("Error SQL:"+sql);
	        throw new SQLException("DBUtilAutoTran.selectPreMutlipleByCount error:" + e);
	    }
	}

    */
/**
     * 获得一定数量结果集查询
     * @param sql
     * @param count
     * @return
     * @throws SQLException
     *//*

    public DBRow[] selectMutlipleByCount(String sql,int count)
	    throws SQLException
	{
	    try
	    {
	    	PageCtrl pc = new PageCtrl();
	    	pc.setPageNo(1);
	    	pc.setPageSize(count);
	    	return(selectMutliple(sql,pc));
	    }
	    catch(Exception e)
	    {
	    	log.error("DBUtilAutoTran.selectMutlipleByCount error:" + e);
	    	log.error("Error SQL:"+sql);
	        throw new SQLException("DBUtilAutoTran.selectMutlipleByCount error:" + e);
	    }
	}
    
    */
/**
     * 通用查询带翻页，带order by
     * @param table
     * @param orderBy		格式：order by id asc或 id asc
     * @param pc
     * @return
     * @throws SQLException
     *//*

    private DBRow[] select(String table, String orderBy, PageCtrl pc)
        throws SQLException
    {
        String orderByStr = "order by";
        if(isBlank(orderBy))
            return selectMutliple("select * from " + table, pc);
        if(orderBy.length() > orderByStr.length())
        {
            String head = orderBy.substring(0, orderByStr.length());
            if(head.equalsIgnoreCase(orderByStr))
                return selectMutliple("select * from " + table + " " + orderBy, pc);
        }
        return selectMutliple("select * from " + table + " " + orderByStr + " " + orderBy, pc);
    }

    */
/**
     * 通用查询带order by
     * @param sql
     * @param orderBy 格式：order by id asc或 id asc
     * @return
     * @throws SQLException
     *//*

    private DBRow[] select(String sql, String orderBy)
        throws SQLException
    {
        return select(sql, orderBy, null);
    }

    */
/**
     * 通用查询
     * @param sql
     * @return
     * @throws SQLException
     *//*

    public DBRow[] select(String tableName)
        throws SQLException
    {
        return select(tableName, null, null);
    }

    */
/**
     * 把ResultSet转换为dbfield，设置字段名和字段数据类型
     * @param rs
     * @return
     * @throws Exception
     *//*

    private DBObjectType[] getFieldInfos(ResultSet rs)
        throws Exception
      {
          if(rs == null) return null;
          
          DBObjectType dbField[] = null;
          ResultSetMetaData rsmd = null;
          
          try
          {
        	  rsmd = rs.getMetaData();
              
              int cols_size = rsmd.getColumnCount();
              dbField = new DBObjectType[cols_size];
              for(int i = 1; i <= cols_size; i++)
              {
                  DBObjectType dbField2 = new DBObjectType();
                  dbField2.setFieldName(rsmd.getColumnName(i));
                  int sqlDataType = rsmd.getColumnType(i);
                  
                  //System.out.println(ll11ll11l1l1ll1111ll11l11l1l1l1l1l1l.getColumnName(i) + " = " +ll11ll11l1l1ll1l11l11l1l1l1l1l1l);                  dbField2.setFieldType(getInnerDataType(sqlDataType));
                  dbField[i - 1] = dbField2;
                  
                  //System.out.println("->"+ll11ll11l1l1ll1111ll11l11l1l1l1l1l1l.getTableName(i));
              }

          }
          catch(SQLException e)
          {
          	  log.error("DBUtilAutoTran.getFieldInfos error:not get metadata" + e);
              throw new Exception("DBUtilAutoTran.getFieldInfos error:not get metadata" + e);
          }
          finally
          {
        	  rsmd = null;
          }
          
          return dbField;
      }

    */
/**
     * 把ResultSet转换为dbfield，设置字段名和字段数据类型
     * @param tablename
     * @return
     * @throws Exception
     *//*

//    public ll1l11ll1l1l1111111111[] getFieldInfos(String tablename)
//        throws Exception
//      {
//          if(tablename == null)
//          {
//              return null;
//          }
//          ll1l11ll1l1l1111111111 dbField[] = null;
//          
//          ResultSet rs = null;
//          Statement stmt = null;
//          try
//          {
//              this.conn = dbConnectionManager.getConnection();
//              
//              stmt = conn.createStatement();
//              String sql = "select * from " + tablename;
//              rs = stmt.executeQuery(sql);
//
//              if( rs == null )
//              {
//                   closeConn(rs,stmt,conn);
//                   return null;
//              }
//
//              ResultSetMetaData rsmd = rs.getMetaData();
//              int cols_size = rsmd.getColumnCount();
//              dbField = new ll1l11ll1l1l1111111111[cols_size];
//              for(int i = 1; i <= cols_size; i++)
//              {
//                  ll1l11ll1l1l1111111111 dbField2 = new ll1l11ll1l1l1111111111();
//                  dbField2.setFieldName(rsmd.getColumnName(i));
//                  int sqlDataType = rsmd.getColumnType(i);
//
//                  */
/*
//                  System.out.println("fieldname:" + ll1l11ll1l1l1ll11ll1111l1l1l1l1.getFieldName());
//                  System.out.println("fieldtype:" + ll11ll11l1l1ll1l11l11l1l1l1l1l1l);
//                  *//*

//
//                  dbField2.setFieldType( getInnerDataType(sqlDataType) );
//                  dbField[i - 1] = dbField2;
//                  
//              }
//          }
//          catch(SQLException e)
//          {
//          	  log.error("DBUtilAutoTran.getFieldInfos error:not get metadata" + e);
//              throw new Exception("not get metadata", e);
//          }
//          finally
//		  {
//              closeConn(rs, stmt, conn);            	
//		  }
//          
//          return dbField;
//      }

    */
/**
     * 内部数据库字段类型转换
     * @param ll11ll11l1l1ll1l11l11l1l1l1l1l1l
     * @return
     *//*

    private int getInnerDataType(int ll11ll11l1l1ll1l11l11l1l1l1l1l1l)
       {
           int innerDataType = -1;
           switch(ll11ll11l1l1ll1l11l11l1l1l1l1l1l)
           {
           case -7:
           case -6:
           case -5:
           case 2: 
           case 3: 
           case 4:
           case 5: 
           case 6: 
           case 7:
           case 8:
               innerDataType = 1;		//数值型
               break;

           case -1:
           case 1: 
           case 12: 
               innerDataType = 2;		//字符串型
               break;

           case 91: 
           case 92: 
           case 93: 
               innerDataType = 8;		//日期类型
               break;

           case -4:
           case -3:
           case -2:
           case 1111:
           case 2000:
           case 2003:
           case 2004:
           case 2005:
               innerDataType = 4;		//object类型
               break;

           case 0: 
           case 16: 
           case 2001:
           case 2002:
           case 2006:
               innerDataType = -1;		//不支持类型
               break;
           }
           return innerDataType;
       }

    */
/**
     * 关闭数据库连接等
     * @param rs
     * @param stmt
     * @param conn
     *//*

    protected void closeConn(ResultSet rs, Statement stmt, Connection conn)
    {
        try
        {
            if(rs!=null)
            {
            	rs.close();
            	rs = null;
            }
        }
        catch(SQLException e)
        {

        }

        try
        {
            if(stmt != null)
            {
                stmt.close();
                stmt = null;
            }
        }
        catch(SQLException e)
        {

        }
        
        try
        {
			if(conn != null)
			{
				//不在事务中，直接关闭数据库。在事务中，由事务管理关闭
				if (!TransactionThread.isInTransaction())
				{
					DataSourceUtils.releaseConnection(conn, dataSource);//用spring的工具关闭数据库
				}
			}
        }
        catch(Exception e)
        {

        }
    }

    
    protected void closeConn(ResultSet rs, CallableStatement stmt, Connection conn)
    {
        try
        {
            if(rs!=null)
            {
            	rs.close();
            }
        }
        catch(SQLException e)
        {

        }

        try
        {
            if(stmt != null)
            {
                stmt.close();
            }
        }
        catch(SQLException e)
        {

        }
        
        try
        {
			if(conn != null)
			{
				//不在事务中，直接关闭数据库。在事务中，由事务管理关闭
				if (!TransactionThread.isInTransaction())
				{
					DataSourceUtils.releaseConnection(conn, dataSource);//用spring的工具关闭数据库
				}
			}
        }
        catch(Exception e)
        {

        }

    }
    
    
    */
/**
     * 判断字符串是否为空
     * @param str
     * @return
     *//*

   private boolean isBlank(String str)
    {
        if( (str == null) || (str.trim().length() == 0))
        {
            return true;
        }
        else
        {
            return false;
        }
    }

   */
/**
    * 把hashmap转换为dbrow
    * @param hashMap
    * @param tablename
    * @return
    * @throws Exception
    *//*

//    public DBRow hashMap2DBRow(HashMap hashMap, String tablename)
//        throws Exception
//    {
//        if( hashMap == null )
//        {
//            return new DBRow();
//        }
//        else
//        {
//        	//获得这个表的dbfield
//            ll1l11ll1l1l1111111111[] dbField = this.getFieldInfos( tablename );
//            DBRow data =new DBRow();
//            Iterator iterator = hashMap.keySet().iterator();
//            while( iterator.hasNext() )
//            {
//                String key = (String)iterator.next();
//                int type = this.getFieldType(key, dbField );
//                switch(type)
//                {
//                    case 1: // int
//                        data.add(key,
//                           Integer.parseInt(String.valueOf(hashMap.get(key))));
//                        break;
//                    case 2:  //long
//                        data.add(key,
//                           Long.parseLong(String.valueOf(hashMap.get(key))));
//                        break;
//
//                    case 3: //float
//                        data.add(key,
//                           Long.parseLong(String.valueOf(hashMap.get(key))));
//                        break;
//
//                    case 4: //double
//                        data.add(key,
//                           Long.parseLong(String.valueOf(hashMap.get(key))));
//                        break;
//                    case 5: //String
//                        data.add(key,
//                           Long.parseLong(String.valueOf(hashMap.get(key))));
//                        break;
//                    case 6: // object
//                        data.add(key, hashMap.get(key) );
//                        break;
//                }
//
//            }
//
//            return data;
//        }
//    }

    */
/**
     * 获得字段类型
     * @param fieldName
     * @param dbField
     * @return
     *//*

    private int getFieldType(String fieldName, DBObjectType[] dbField)
    {
        int fieldType = 0;
        if( (fieldName == null ) || (dbField == null) )
        {
            return fieldType;
        }

        if( dbField.length == 0 )
        {
            return fieldType;
        }

        int size = dbField.length;

        for( int i = 0; i < size; i ++ )
        {
            if( dbField[i].getFieldName().equals( fieldName ) )
            {
            	fieldType = dbField[i].getFieldType();
                i = size;
            }
        }

        return fieldType;

    }
    
    */
/**
     * 更新数据
     * @param where			条件
     * @param msql			a=a+1
     * @param table
     * @throws Exception
     *//*

    public void update(String where,String msql,String table)
    	throws Exception
    {
    	update(where,msql,table,true);
    }
    
    */
/**
     * 更新数据(可控制是否马上清空缓存)
     * @param where				条件
     * @param msql				a=a+1
     * @param table
     * @param clean				true为马上清空
     * @throws Exception
     *//*

    public void update(String where,String msql,String table,boolean clean)
		throws Exception
	{
		PreparedStatement pstmt = null;
		String sql=null;
		Connection conn = null;
		
		try
		{
			sql = "update "+table+" set "+msql+" " + where;
			conn = DataSourceUtils.getConnection(dataSource);
			pstmt = conn.prepareStatement(sql);
			pstmt.executeUpdate();
		}
		catch (Exception e)
		{
			log.error("DBUtilAutoTran.update error:" + e);
			log.error("DBUtilAutoTran.update errorSQL:" + sql);
			throw new Exception("DBUtilAutoTran.update error:" + e);
		}
	    finally
		{
				closeConn(null, pstmt, conn);
		}
	    
	    
	    
		if (TransactionThread.isInTransaction())
		{
			TransactionThread.addTransactionUpdateTable(table);			
		}
	}
    
    */
/**
     * 获得所有表名
     * @return
     * @throws Exception
     *//*

	public ArrayList getTableNames()
		throws Exception
	{
		Connection conn = null;
		
		try
		{
			ArrayList tablesAl = new ArrayList(); 
			
			conn = DataSourceUtils.getConnection(dataSource);
			
			DatabaseMetaData dm = conn.getMetaData();
			ResultSet ll1111l1l1ll11ll11l11l1l1l1l1 = dm.getTables("","","",null);
			while ( ll1111l1l1ll11ll11l11l1l1l1l1.next() )
			{
				tablesAl.add(ll1111l1l1ll11ll11l11l1l1l1l1.getString("TABLE_NAME"));
			}			
				
			return(tablesAl);			
		}
		catch (Exception e)
		{
			log.error("DBUtilAutoTran.getTableNames error:" + e);
			throw new Exception("DBUtilAutoTran.getTableNames error:" + e);
		}
        finally
		{
                closeConn(null, null, conn);        		
		}
	}
	
	*/
/**
	 * 获得sequence
	 * @param tablename
	 * @return
	 * @throws Exception
	 *//*

	public long getSequance(String tablename)
		throws Exception
	{
		//每个表有一个锁
		synchronized(tablename)
		{
			Statement ps = null;
			Statement ps2 = null;
			Statement ps3 = null;
			Statement ps4 = null;
			
		    ResultSet rs = null;
		    ResultSet rs2 = null;
		    
		    Connection conn = null;
		    
		    try
		    {
		    	conn = DataSourceUtils.getConnection(dataSource);
		    	
		    	String sql = "select * from " + ConfigBean.getStringValue("sequence_table") + " where tablename='" + tablename + "'";
		        ps = conn.createStatement();               
		        rs = ps.executeQuery(sql);
		        //先检查表名是否已经创建了sequance
		        if (rs.next())
		        {
		        	String incSql = "update " + ConfigBean.getStringValue("sequence_table") + " set seq=seq+1 where tablename='" + tablename + "'";
		        	ps2 = conn.createStatement();           
		        	ps2.executeUpdate(incSql);
		        	
		        	String newSequanceSql = "select * from " + ConfigBean.getStringValue("sequence_table") + " where tablename='" + tablename + "'";
		        	ps3 = conn.createStatement();               
				    rs2 = ps3.executeQuery(newSequanceSql);
				    
				    if (rs2.next())
				    {
				    	return(rs2.getLong("seq"));
				    }
				    else
				    {
				    	return(0);
				    }
		        }
		        else
		        {
		        	String incSql = "insert into " + ConfigBean.getStringValue("sequence_table") + " values('" + tablename + "',"+ConfigBean.getIntValue("org_sequence")+")";
		        	ps4 = conn.createStatement();           
		        	ps4.executeUpdate(incSql);
		        	
		        	return(ConfigBean.getIntValue("org_sequence"));
		        }
		    }
		    catch(Exception e)
		    {
		        throw new SQLException("getSequance error:" + e);
		    }
		    finally
			{
		        closeConn(rs, ps, conn);   
		        closeConn(rs2, null, null);   
		        closeConn(null, ps2, null);  
		        closeConn(null, ps3, null);  
		        closeConn(null, ps4, null);  
			}
		}
	}

	*/
/**
	 * 判断当前表的sequence是否存在
	 * @param tablename
	 * @return
	 * @throws Exception
	 *//*

	private boolean sequanceIsExist(String tablename)
		throws Exception
	{
		try
		{
			DBRow data = selectSingle("select * from " + ConfigBean.getStringValue("sequence_table") + " where tablename='" + tablename + "'");
			
			if ( data != null )
			{
				return(true);
			}
			else
			{
				return(false);
			}
		}
		catch (SQLException e)
		{
			log.error("DBUtilAutoTran.sequanceIsExist error:" + e);
			throw new Exception("DBUtilAutoTran.sequanceIsExist error:" + e);
		}
	}

	*/
/**
	 * ��sequance��把表的sequence加1
	 * @param tablename
	 * @return
	 * @throws Exception
	 *//*

	private long increaseSequance(String tablename)
		throws Exception
	{
		try
		{
			update("where tablename='" + tablename + "'","seq=seq+1",ConfigBean.getStringValue("sequence_table"));
			DBRow data = selectSingle("select * from " + ConfigBean.getStringValue("sequence_table") + " where tablename='" + tablename + "'");
			return(data.get("seq",0));
		}
		catch (Exception e)
		{
			log.error("DBUtilAutoTran.increaseSequance error:" + e);
			throw new Exception("DBUtilAutoTran.increaseSequance error:" + e);
		}
	}
	
	*/
/**
	 * �½�创建sequence
	 * @param tablename
	 * @return
	 * @throws Exception
	 *//*

	private long createSequance(String tablename)
		throws Exception
	{
		try
		{
			DBRow data = new DBRow();
			data.add("tablename",tablename);
			data.add("seq",ConfigBean.getIntValue("org_sequence"));
			insert(ConfigBean.getStringValue("sequence_table"),data);
			return(ConfigBean.getIntValue("org_sequence"));
		}
		catch (Exception e)
		{
			log.error("DBUtilAutoTran.createSequance error:" + e);
			throw new Exception("DBUtilAutoTran.createSequance error:" + e);
		}
	}

	*/
/**
	 * 获得数据库类型
	 * @return
	 * @throws Exception
	 *//*

	public String getDatabaseType()
		throws Exception
	{
		Connection conn = null;
		String ll1ll11ll111ll1l1l1l1l1l1l1111111 = "";
		try
		{			
			conn = DataSourceUtils.getConnection(dataSource);
			
			DatabaseMetaData ll1ll11llll111ll1l1l1l1l1l1l1111111 = conn.getMetaData();
			ll1ll11ll111ll1l1l1l1l1l1l1111111 = ll1ll11llll111ll1l1l1l1l1l1l1111111.getDatabaseProductName();
			return(ll1ll11ll111ll1l1l1l1l1l1l1111111);
		}
		catch (Exception e)
		{
			log.error("DBUtilAutoTran.getDatabaseType error:" + e);
			throw new Exception("DBUtilAutoTran.getDatabaseType error:" + e);
		}
		finally
		{
				closeConn(null,null,conn);				
		}
	}
	

//	private boolean isDataRule(String val)
//	{
//		String t[] = val.split(" ");
//		String ymd[] = t[0].split("-");
//		
//		if ( (ymd.length != 3) || (ymd[0].length() != 4) || (ymd[1].length() != 2) || (ymd[2].length() != 2) )
//		{
//			return(false);
//		}
//
//		try
//		{
//			Integer.parseInt(ymd[0]);
//		}
//		catch (NumberFormatException e)
//		{
//			return(false);
//		}
//		try
//		{
//			Integer.parseInt(ymd[1]);
//		}
//		catch (NumberFormatException e)
//		{
//			return(false);
//		}
//		try
//		{
//			Integer.parseInt(ymd[2]);
//		}
//		catch (NumberFormatException e)
//		{
//			return(false);
//		}
//		
//		return(true);
//	}

	*/
/**
	 * �½�为某字段值加N
	 * @param table
	 * @param where
	 * @param field
	 * @param val
	 * @param clean
	 * @throws Exception
	 *//*

	public void increaseFieldVal(String table,String where,String field,long val,boolean clean)
		throws Exception
	{
		try 
		{
			update(where,field+"="+field+"+"+val,table,clean);
		} 
		catch (Exception e) 
		{
			log.error("DBUtilAutoTran.increaseFieldVal error:" + e);
			throw new Exception("DBUtilAutoTran.increaseFieldVal error:" + e);
		}
	}

	*/
/**
	 * �½�为某字段值减N
	 * @param table
	 * @param where
	 * @param field
	 * @param val
	 * @param clean
	 * @throws Exception
	 *//*

	public void decreaseFieldVal(String table,String where,String field,long val,boolean clean)
		throws Exception
	{
		try
		{
			//String sql = "update "+ll11l11llll1l1l1llllll11l11l1l1l1l1+" set "+ll11l11l1l1ll11ll1111l1l1l1l1+"="+ll11l11l1l1ll11ll1111l1l1l1l1+"-"+val+" "+where;
			update(where,field+"="+field+"-"+val,table,clean);
		}
		catch (Exception e)
		{
			log.error("DBUtilAutoTran.decreaseFieldVal error:" + e);
			throw new Exception("DBUtilAutoTran.decreaseFieldVal error:" + e);
		}
	}
	
	*/
/**
	 * 根据表名，参数和翻页数据生成sql key，用来作为结果集缓存key
	 * @param table
	 * @param para
	 * @param pc
	 * @return
	 *//*

    private String makeSqlKey(String table,DBRow para,PageCtrl pc)
    {
    	String key = table;
    	StringBuffer ll1ll11ll111ll11l1l1l1l1111111 = new StringBuffer("");
    	
    	//参数不为空，把参数追加到KEY上
    	if (para!=null)
    	{
        	ArrayList fieldNames = para.getFieldNames();
        	for (int i=0; i<fieldNames.size(); i++)
        	{
        		ll1ll11ll111ll11l1l1l1l1111111.append(fieldNames.get(i));
        		ll1ll11ll111ll11l1l1l1l1111111.append("=");
        		ll1ll11ll111ll11l1l1l1l1111111.append(para.getString(fieldNames.get(i).toString()));
        		ll1ll11ll111ll11l1l1l1l1111111.append("&");
        	}
        	key += "|"+ll1ll11ll111ll11l1l1l1l1111111.toString();
    	}
    	
    	//翻页不为空，把当前页追加到KEY上
    	if ( pc!=null )
    	{
    		key += "|p="+pc.getPageNo();
    	}
    	
    	return(key);
    }
	
    */
/**
     * 安装程序批量导入SQL
     * @param sql
     * @throws Exception
     *//*

    public void batchUpdate(String sql[])
		throws Exception
	{
    	Connection conn = null;
		Statement stmt = null;
		int i=0;
		
		try
		{
			conn = DataSourceUtils.getConnection(dataSource);
			
			stmt = conn.createStatement();
			
			for (; i<sql.length; i++)
			{
				//System.out.println(sql[i]);
				if (sql[i]!=null&&!sql[i].equals(""))
				{
					stmt.addBatch(sql[i]);					
				}
			}
			stmt.executeBatch();
		}
		catch (Exception e)
		{
			log.error("DBUtilAutoTran.batchUpdate error:" + e);
			log.error("DBUtilAutoTran.batchUpdate errorSQL:" + sql[i-1]);
			throw new Exception("DBUtilAutoTran.batchUpdate error:" + e);
		}
	    finally
		{
				 closeConn(null, stmt, conn);   
		}
	}
    
    */
/**
     * 安装程序使用
     * @param sql
     * @throws Exception
     *//*

    public void updateInstallSQL(String sql)
		throws Exception
	{
    	Connection conn = null;
		Statement stmt = null;

		try
		{
			conn = DataSourceUtils.getConnection(dataSource);
			
			stmt = conn.createStatement();
			stmt.executeUpdate(sql);
		}
		catch (Exception e)
		{
			log.error("DBUtilAutoTran.batchUpdate error:" + e);
			log.error("DBUtilAutoTran.batchUpdate errorSQL:" + sql);
			throw new Exception("DBUtilAutoTran.batchUpdate error:" + e);
		}
	    finally
		{
				 closeConn(null,stmt , conn);   
		}
	}
    
    */
/**
     * 获得数据库主版本
     * @return
     * @throws Exception
     *//*

    public int getDBMajorVersion()
		throws Exception
	{
    	Connection conn = null;
    	
		try
		{
			conn = DataSourceUtils.getConnection(dataSource);
			
			return(conn.getMetaData().getDatabaseMajorVersion());
		}
		catch (Exception e)
		{
			log.error("DBUtilAutoTran.getDBMajorVersion error:" + e);
			throw new Exception(e.getMessage());
		}
	    finally
		{
		        closeConn(null,null,conn);	    		
		}
	}
    
    */
/**
     * 获得数据库子版本
     * @return
     * @throws Exception
     *//*

    public int getDBMinorVersion()
		throws Exception
	{
    	Connection conn = null;
		try
		{
			conn = DataSourceUtils.getConnection(dataSource);
			
			return(conn.getMetaData().getDriverMinorVersion());
		}
		catch (Exception e)
		{
			log.error("DBUtilAutoTran.getDBMinorVersion error:" + e);
			throw new Exception(e.getMessage());
		}
	    finally
		{
		        closeConn(null,null,conn);	    		
		}
	}
    
    */
/**
     * 获得数据库版本
     * @return
     * @throws Exception
     *//*

    public String getDBVersion()
		throws Exception
	{
    	Connection conn = null;
    	
		try
		{
			conn = DataSourceUtils.getConnection(dataSource);
			
			return(conn.getMetaData().getDatabaseProductVersion());
		}
		catch (Exception e)
		{
			log.error("DBUtilAutoTran.getDBMinorVersion error:" + e);
			throw new Exception(e.getMessage());
		}
	    finally
		{
		        closeConn(null,null,conn);	    		
		}
	}
    
    */
/**
     * 获得数据库名称
     * @return
     * @throws Exception
     *//*

    public String getDBName()
		throws Exception
	{
    	Connection conn = null;
    	
		try
		{
			conn = DataSourceUtils.getConnection(dataSource);
			return(conn.getMetaData().getDatabaseProductName());
		}
		catch (Exception e)
		{
			log.error("DBUtilAutoTran.getDBName error:" + e);
			throw new Exception(e.getMessage());
		}
	    finally
		{
		        closeConn(null,null,conn);	    		
		}
	}
    
    */
/**
     * 获得所有表名
     * @return
     * @throws Exception
     *//*

    public ArrayList getAllTables()
		throws Exception
	{
    	Connection conn = null;
    	
		try
		{
			ArrayList al = new ArrayList();
			
			conn = DataSourceUtils.getConnection(dataSource);
			DatabaseMetaData dmd = conn.getMetaData();

			String[] types = {"TABLE"}; 
			ResultSet rs = dmd.getTables(null,null,"%",types);

			while(rs.next())
			{
				al.add(rs.getString(3));
			}
			rs.close();
			rs = null;
			
			return(al);
		}
		catch (Exception e)
		{
			log.error("DBUtilAutoTran.getAllTables error:" + e);
			throw new Exception(e.getMessage());
		}
	    finally
		{
	    		closeConn(null,null,conn);          
		}
	}
    
    */
/**
     * 为某字段加N
     * @param table
     * @param where
     * @param field
     * @param val
     * @param clean
     * @throws Exception
     *//*

	public void increaseFieldVal(String table,String where,String field,double val,boolean clean)
		throws Exception
	{
		try 
		{
			update(where,field+"="+field+"+"+val,table,clean);
		} 
		catch (Exception e) 
		{
			log.error("DBUtilAutoTran.increaseFieldVal error:" + e);
			throw new Exception("DBUtilAutoTran.increaseFieldVal error:" + e);
		}
	}
    
	*/
/**
	 * 获得某个表的ResultSetMetaData
	 * @param tbName
	 * @return
	 * @throws Exception
	 *//*

	public ResultSetMetaData getTableResultSetMetaData(String tbName)
		throws Exception
	{
		ResultSet rs = null;
		Statement stmt = null;
		Connection conn = null;
		
		try 
		{
			conn = DataSourceUtils.getConnection(dataSource);
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from " + tbName);
			
			return(rs.getMetaData());
		} 
        catch(SQLException e)
        {
        	log.error("DBUtilAutoTran.ResultSetMetaData error:" + e);
            throw new Exception("DBUtilAutoTran.ResultSetMetaData error:" + e);
        }
        finally
        {
        		closeConn(rs, stmt, conn);
        	
        }		
	}
	
	*/
/**
	 * 获得DatabaseMetaData
	 * @return
	 * @throws Exception
	 *//*

	public DatabaseMetaData getDatabaseMetaData()
		throws Exception
	{
		Connection conn = null;
		
		try
		{
			conn = DataSourceUtils.getConnection(dataSource);
			DatabaseMetaData dmd = conn.getMetaData();
			return(dmd);
		}
		catch (Exception e)
		{
			log.error("DBUtilAutoTran.getDatabaseMetaData error:" + e);
			throw new Exception("DBUtilAutoTran.getDatabaseMetaData error:" + e);
		}
	    finally
		{
		        closeConn(null,null,conn);	    		
		}
	}
	
	*/
/**
	 * 增加数值字段的值
	 * @param table
	 * @param where
	 * @param field
	 * @param val
	 * @throws Exception
	 *//*

	public void increaseFieldVal(String table,String where,String field,double val)
		throws Exception
	{
		try 
		{
			update(where,field+"="+field+"+"+val,table);
		} 
		catch (Exception e) 
		{
			log.error("DBUtil.increaseFieldVal error:" + e);
			throw new Exception("DBUtil.increaseFieldVal error:" + e);
		}
	}
	
	public void increaseFieldVal(String table,String where,String field,float val)
		throws Exception
	{
		try 
		{
			update(where,field+"="+field+"+"+val,table);
		} 
		catch (Exception e) 
		{
			log.error("DBUtil.increaseFieldVal error:" + e);
			throw new Exception("DBUtil.increaseFieldVal error:" + e);
		}
	}
	
	*/
/**
	 * 减少数值字段的值
	 * @param table
	 * @param where
	 * @param field
	 * @param val
	 * @throws Exception
	 *//*

	public void decreaseFieldVal(String table,String where,String field,double val)
		throws Exception
	{
		try
		{
			update(where,field+"="+field+"-"+val,table);
		}
		catch (Exception e)
		{
			log.error("DBUtil.decreaseFieldVal error:" + e);
			throw new Exception("DBUtil.decreaseFieldVal error:" + e);
		}
	}
	
	public void decreaseFieldVal(String table,String where,String field,float val)
		throws Exception
	{
		try
		{
			update(where,field+"="+field+"-"+val,table);
		}
		catch (Exception e)
		{
			log.error("DBUtil.decreaseFieldVal error:" + e);
			throw new Exception("DBUtil.decreaseFieldVal error:" + e);
		}
	}
	
	*/
/**
	 * 加减数值字段的值
	 * @param table
	 * @param where
	 * @param field
	 * @param val 正数为加，负数为减
	 * @throws Exception
	 *//*

	public void addSubFieldVal(String table,String where,String field,double val)
		throws Exception
	{
		try
		{
			update(where,field+"="+field+"+"+val,table);
		}
		catch (Exception e)
		{
			log.error("DBUtil.decreaseFieldVal error:" + e);
			throw new Exception("DBUtil.decreaseFieldVal error:" + e);
		}
	}
	
	public void addSubFieldVal(String table,String where,String field,float val)
		throws Exception
	{
		try
		{
			update(where,field+"="+field+"+"+val,table);
		}
		catch (Exception e)
		{
			log.error("DBUtil.decreaseFieldVal error:" + e);
			throw new Exception("DBUtil.decreaseFieldVal error:" + e);
		}
	}
	
	*/
/**
	 * 调用存储过程
	 * @param proName
	 * @return
	 * @throws Exception
	 *//*

	public CallableStatement CallStringSingleResultProcedure(String proName)
		throws Exception
	{
		Connection conn = null;
		CallableStatement stmt = null;

		try
		{
			//java调用mysql存储过程{call nextid (?)} // 不能写为{?= call nextid (?)}
			//{?=call test.get_next_value (?)} 
			//proName = "{ call testpro(?,?,?,?,?,?,?,?,?) }";
			conn = DataSourceUtils.getConnection(dataSource);
			stmt = conn.prepareCall("{"+proName+"}");
	
			stmt.execute();
		
			return(stmt);
		}
		catch (Exception e)
		{
			throw new Exception("CallStringSingleResultProcedure error:" + e);
		}
	    finally
		{
        		closeConn(null, stmt, conn);
		}
	}
		
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	*/
/**
	 * 获得MYSQL创建表SQL
	 * @param tbName
	 * @return
	 * @throws Exception
	 *//*

	public String getMysqlCreateTableSQL(String tbName)
		throws Exception
	{
		try
		{
			String type,sql,colSize;
			String typeSp[];
			StringBuffer sb = new StringBuffer("");

			ArrayList primaryKey = new ArrayList();		//主键
			ArrayList notSize = new ArrayList();		//没有长度
			notSize.add("datetime");		
			notSize.add("text");
			notSize.add("date");			

			DatabaseMetaData dbmd = getDatabaseMetaData();
			ResultSet rs = dbmd.getColumns(null, "HR", tbName, "%");
			
			sb.append("DROP TABLE IF EXISTS `");
			sb.append(tbName);
			sb.append("`;\n");

			sb.append("CREATE TABLE  `");
			sb.append(tbName);
			sb.append("` (");
			while ( rs.next() )
			{
				type = rs.getString(6);			//字段类型
				typeSp = type.split(" ");
				
				sb.append("`");
				sb.append(rs.getString(4));
				sb.append("` ");

				if (!rs.getString(9).equals("0"))//判断小数位
				{
					colSize = rs.getString(7)+","+rs.getString(9);
				}
				else
				{
					colSize = rs.getString(7);
				}
				
				if (typeSp.length>1)//包含unsign的排除
				{
					type = typeSp[0] + "("+colSize+") " + typeSp[1];						
				}
				else
				{
					if (!notSize.contains(type))//排除不需要长度的类型
					{
						type = type + "("+colSize+") ";
					}
				}
				sb.append(type);
				
				if ( rs.getString(11).equals("0") )
				{
					sb.append(" NOT NULL,");
				}
				else
				{
					sb.append(" NULL,");
				}
			}

			//索引
			String tmp1="",tmp2="";
			rs = dbmd.getIndexInfo(null, "HR", tbName, false, false);
			DBRow key = new DBRow();
			while(rs.next())
			{
				if ( rs.getString(6).equals("PRIMARY") )
				{
					primaryKey.add(rs.getString(9));
				}
				else
				{
					if (key.getString(rs.getString(6)).equals(""))
					{
						key.add(rs.getString(6), rs.getString(9));
					}
					else
					{
						String t = key.getString(rs.getString(6));
						t += ","+rs.getString(9);
						key.add(rs.getString(6), t);
					}
				}				
			}
			
			if ( primaryKey.size()>0 )
			{
				int i=0;
				tmp1 += "PRIMARY KEY  (";
				for (;i<primaryKey.size()-1; i++)
				{
					tmp1 += "`";
					tmp1 += primaryKey.get(i);
					tmp1 += "`,";
				}
				tmp1 += "`";
				tmp1 += primaryKey.get(i);
				tmp1 += "`),";
			}
			
			if ( key.getFieldNames().size()>0 )
			{						
				for (int i=0; i<key.getFieldNames().size(); i++)
				{
					int j=0;
					tmp2 += "KEY `"+key.getFieldNames().get(i)+"` (";
					String tt[] = key.getString( key.getFieldNames().get(i).toString() ).split(",");

					for (;j<tt.length-1; j++)
					{
						tmp2 += "`";
						tmp2 += tt[j];
						tmp2 += "`,";
					}
					tmp2 += "`";
					tmp2 += tt[j];
					tmp2 += "`),";
				}
			}

			sb.append(tmp1+tmp2);
			
			sb.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;\n");


			sql = sb.toString();
			sql = StrUtil.regReplace(sql, ",\\)", "\\)");
			
			return(sql);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			//log.error("DBUtilAutoTran.getMysqlCreateTableSQL("+tbName+") error:" + e);
			throw new Exception("DBUtilAutoTran.getMysqlCreateTableSQL("+tbName+") error:" + e);
		}
		
		
	}

    
   
    
    */
/**
     * 直接执行SQL语句
     * @param sql
     * @throws SQLException
     *//*

    public void executeSQL(String sql)
    	throws SQLException
    {
    	Statement ps = null;
    	Connection conn = DataSourceUtils.getConnection(dataSource);
    	
    	
    	try 
    	{
			ps = conn.createStatement();
			ps.executeUpdate(sql);
			closeConn(null, ps, null);
		}
    	catch (SQLException e) 
		{
			 //System.out.println(e.getMessage());
			 throw new SQLException(e.getMessage());
		}
		finally
		{
		    closeConn(null, ps,null);
		}
    }
    
    
	    
    
    public void updateOrderScIdTMP()
	    throws SQLException
	{
		Statement ps = null;
		Statement ps2 = null;
	    ResultSet rs = null;
	    Connection conn = null;
	    
	    try
	    {
	    	DBRow shippingCompany = new DBRow();
	    	shippingCompany.add("DHL", "100002");
	    	shippingCompany.add("EMS", "100003");
	    	shippingCompany.add("FEDEX", "100008");
	    	shippingCompany.add("TNT", "100004");
	    	shippingCompany.add("USPS", "100006");
	    	shippingCompany.add("USPS-F", "100009");
	    	shippingCompany.add("USPS-P", "100006");
	    	shippingCompany.add("上门自提", "100007");
	    	
	    	String sql = "select oid,shipping_name from porder where shipping_name!='' and sc_id=0";
	        conn = DataSourceUtils.getConnection(dataSource);
	
	        ps = conn.createStatement();               
	        rs = ps.executeQuery(sql);
	        int i=0;
	        while(rs !=null && rs.next())
	        {
	        	//System.out.println(i++);
	        	String oid = rs.getString("oid");
	        	String shipping_name = rs.getString("shipping_name");
	        	
	        	ps2 = conn.createStatement();           
	        	ps2.executeUpdate("update porder set sc_id="+shippingCompany.getString(shipping_name)+" where oid="+oid);
	        }
	    }
	    catch(Exception e)
	    {
	    	log.error("DBUtil.updateOrderScIdTMP error:" + e);
	        throw new SQLException("DBUtil.updateOrderScIdTMP error:" + e);
	    }
	    finally
		{
	        closeConn(rs, ps, conn);            
	        closeConn(null, ps2, null);            	
		}
	}
    
    
    public void updateShippingScIdTMP()
	    throws SQLException
	{
		Statement ps = null;
		Statement ps2 = null;
	    ResultSet rs = null;
	    Connection conn = null;
	    
	    try
	    {
	    	String sql = "select sp_id,o.sc_id from porder o,ship_product sp where o.oid=sp.name and o.sc_id>0";
	        conn = DataSourceUtils.getConnection(dataSource);
	
	        ps = conn.createStatement();               
	        rs = ps.executeQuery(sql);
	        int i=0;
	        while(rs !=null && rs.next())
	        {
	        	//System.out.println(i++);
	        	
	        	
	        	ps2 = conn.createStatement();           
	        	ps2.executeUpdate("update ship_product set sc_id="+rs.getString("sc_id")+" where sp_id="+rs.getString("sp_id"));
	        }
	    }
	    catch(Exception e)
	    {
	    	log.error("DBUtil.updateOrderScIdTMP error:" + e);
	        throw new SQLException("DBUtil.updateOrderScIdTMP error:" + e);
	    }
	    finally
		{
	        closeConn(rs, ps, conn);            
	        closeConn(null, ps2, null);            	
		}
	}
    
    public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}
	
	

}











*/
