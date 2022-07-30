package hello.jbdc.repository

import hello.jbdc.Log
import hello.jbdc.domain.Member
import org.springframework.jdbc.support.JdbcUtils
import java.sql.*
import javax.sql.DataSource
import kotlin.jvm.Throws


class MemberRepositoryV2(
  private val dataSource: DataSource
):Log {


  fun save(member: Member): Member{
    val sql: String = "insert into member(member_id, money) values (?, ?)"

    lateinit var con: Connection
    lateinit var pstmt: PreparedStatement

    try{
      con = getConnection()
      pstmt = con.prepareStatement(sql)
      pstmt.setString(1, member.memberId)
      pstmt.setInt(2, member.money)
      pstmt.executeUpdate()
      return member
    } catch(e: SQLException){
        logger.error("db error", e)
        e.printStackTrace()
        throw e
    } finally {
      close(con, pstmt, null);
    }

  }

  @Throws(SQLException::class)
  fun findById(memberId: String): Member{
    val sql = "select * from member where member_id = ?"

    lateinit var con: Connection
    lateinit var pstmt: PreparedStatement
    lateinit var rs: ResultSet

    try{
      con = getConnection()
      pstmt = con.prepareStatement(sql)
      pstmt.setString(1, memberId)

      rs = pstmt.executeQuery()

      if (rs.next()){
        val member = Member(
          memberId = rs.getString("member_id"),
          money = rs.getInt("money")
        )

        return member
      }else{
        throw NoSuchElementException("member not found memberId=" + memberId)
      }
    } catch (e: SQLException){
        logger.error("db error", e)
        throw e
    } finally {
      close(con, pstmt, null)
    }
  }

  @Throws(SQLException::class)
  fun findById(con: Connection, memberId: String): Member{
    val sql = "select * from member where member_id = ?"

    lateinit var pstmt: PreparedStatement
    lateinit var rs: ResultSet

    try{

      pstmt = con.prepareStatement(sql)
      pstmt.setString(1, memberId)

      rs = pstmt.executeQuery()

      if (rs.next()){
        val member = Member(
          memberId = rs.getString("member_id"),
          money = rs.getInt("money")
        )

        return member
      }else{
        throw NoSuchElementException("member not found memberId=" + memberId)
      }
    } catch (e: SQLException){
      logger.error("db error", e)
      throw e
    } finally {
      JdbcUtils.closeResultSet(rs);
      JdbcUtils.closeStatement(pstmt);
    }
  }

  @Throws(SQLException::class)
  fun update(memberId: String, money: Int){
    val sql = "update member set money=? where member_id=?"

    lateinit var con: Connection
    lateinit var pstmt: PreparedStatement

    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setInt(1, money);
      pstmt.setString(2, memberId);

      val resultSize = pstmt.executeUpdate()
      logger.info("resultSize={}", resultSize)
    } catch (e: SQLException){
        logger.error("db error", e)
        throw e
    } finally {
      close(con, pstmt, null)
    }
  }

  @Throws(SQLException::class)
  fun update(con: Connection, memberId: String, money: Int){
    val sql = "update member set money=? where member_id=?"
    lateinit var pstmt: PreparedStatement

    try {
      pstmt = con.prepareStatement(sql);
      pstmt.setInt(1, money);
      pstmt.setString(2, memberId);

      val resultSize = pstmt.executeUpdate()
      logger.info("resultSize={}", resultSize)
    } catch (e: SQLException){
      logger.error("db error", e)
      throw e
    } finally {
      JdbcUtils.closeStatement(pstmt);
    }
  }
  @Throws(SQLException::class)
  fun delete(memberId: String){
    val sql = "delete from member where member_id=?"

    lateinit var con: Connection
    lateinit var pstmt: PreparedStatement

    try {
      con = getConnection();
      pstmt = con.prepareStatement(sql);
      pstmt.setString(1, memberId);

      pstmt.executeUpdate()

    } catch (e: SQLException){
        logger.error("db error", e)
        throw e
    } finally {
      close(con, pstmt, null);
    }
  }


  private fun getConnection(): Connection {
    val con = dataSource.connection
    logger.info("get connection={}, class={}", con, con.javaClass)
    return con
  }

  private fun close(con: Connection?, stmt: Statement?, rs: ResultSet?) {
    JdbcUtils.closeResultSet(rs);
    JdbcUtils.closeStatement(stmt);
    JdbcUtils.closeConnection(con);
  }
}