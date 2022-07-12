package hello.jbdc.repository

import hello.jbdc.Log
import hello.jbdc.connection.DBConnectionUtil
import hello.jbdc.domain.Member
import java.sql.*


class MemberRepositoryV0(

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
      close(con, pstmt, rs);
    }

  }

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
      close(con, pstmt, null)
    }
  }


  private fun getConnection(): Connection {
    return DBConnectionUtil.getConnection()
  }

  private fun close(con: Connection?, stmt: Statement?, rs: ResultSet?) {
    if (rs != null) {
      try {
        rs.close()
      } catch (e: SQLException) {
        logger.info("error", e)
      }
    }
    if (stmt != null) {
      try {
        stmt.close()
      } catch (e: SQLException) {
        logger.info("error", e)
      }
    }
    if (con != null) {
      try {
        con.close()
      } catch (e: SQLException) {
        logger.info("error", e)
      }
    }
  }
}