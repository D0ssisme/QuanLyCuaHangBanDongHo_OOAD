package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.CallableStatement;  // fix lỗi CallableStatement
import java.sql.Types; 

import DTO.SanPhamDTO;
import config.JDBCUtil;

public class SanPhamDAO implements DAOinterface<SanPhamDTO> {

    public static SanPhamDAO getInstance() {
        return new SanPhamDAO();
    }

    @Override
  public int insert(SanPhamDTO t) {
    int result = 0;

    try (Connection con = JDBCUtil.getConnection();
         CallableStatement cs = con.prepareCall("{call InsertSanPham(?,?,?,?,?,?,?,?)}")) {

        cs.setString(1, t.getTEN());
        cs.setString(2, t.getHINHANH());
        cs.setInt(3, t.getMNCC());
        cs.setString(4, t.getTHUONGHIEU());

        // xử lý nullable giống code cũ
        if (t.getNAMSANXUAT() != null) {
            cs.setInt(5, t.getNAMSANXUAT());
        } else {
            cs.setNull(5, Types.INTEGER);
        }

        cs.setDouble(6, t.getGIANHAP());
        cs.setDouble(7, t.getGIABAN());
        cs.setInt(8, t.getTHOIGIANBAOHANH());

        result = cs.executeUpdate();

    } catch (SQLException ex) {
        Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
}
 @Override
public int update(SanPhamDTO t) {
    int result = 0;

    String sql = "{call UpdateSanPham(?, ?, ?, ?, ?, ?, ?, ?, ?)}";

    try (Connection con = JDBCUtil.getConnection();
         CallableStatement cst = con.prepareCall(sql)) {

        cst.setString(1, t.getTEN());
        cst.setString(2, t.getHINHANH());
        cst.setInt(3, t.getMNCC());
        cst.setString(4, t.getTHUONGHIEU());

        if (t.getNAMSANXUAT() != null) {
            cst.setInt(5, t.getNAMSANXUAT());
        } else {
            cst.setNull(5, java.sql.Types.INTEGER);
        }

        cst.setDouble(6, t.getGIANHAP());
        cst.setDouble(7, t.getGIABAN());
        cst.setInt(8, t.getTHOIGIANBAOHANH());
        cst.setInt(9, t.getMSP());

        result = cst.executeUpdate();

    } catch (SQLException ex) {
        Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, null, ex);
    }

    return result;
}
        @Override
      public int delete(String t) {
          int result = 0;

          String sql = "{call DeleteSanPham(?)}";

          try (Connection con = JDBCUtil.getConnection();
               CallableStatement cst = con.prepareCall(sql)) {

              cst.setInt(1, Integer.parseInt(t)); // MSP là int

              result = cst.executeUpdate();

          } catch (SQLException ex) {
              Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, null, ex);
          }

          return result;
      }
    @Override
    public ArrayList<SanPhamDTO> selectAll() {
        // Gọi selectAll với MCN mặc định là null để lấy tất cả (không lọc chi nhánh)
        return selectAllByMCN(null);
    }

    /**
     * Load sản phẩm có tồn kho ở chi nhánh cụ thể
     * @param mcn Mã chi nhánh. Nếu null, lấy tất cả sản phẩm
     * @return Danh sách sản phẩm với số lượng tồn kho
     */

    public ArrayList<SanPhamDTO> selectAllByMCN(String mcn) {
        ArrayList<SanPhamDTO> result = new ArrayList<>();

        try (Connection con = JDBCUtil.getConnection(mcn)) {

            String sql = "{CALL GetAllSanPham}";
            CallableStatement cs = con.prepareCall(sql);

            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                int msp = rs.getInt("MSP");
                String ten = rs.getString("TEN");
                String hinhanh = rs.getString("HINHANH");
                int mncc = rs.getInt("MNCC");
                String thuonghieu = rs.getString("THUONGHIEU");

                Integer namsanxuat = rs.getObject("NAMSANXUAT") != null 
                        ? rs.getInt("NAMSANXUAT") 
                        : null;

                double gianhap = rs.getDouble("GIANHAP");
                double giaban = rs.getDouble("GIABAN");
                int thoigianbaohanh = rs.getInt("THOIGIANBAOHANH");
                int soluong = rs.getInt("SOLUONG");

                SanPhamDTO sp = new SanPhamDTO(
                    msp, ten, hinhanh, mncc, thuonghieu,
                    namsanxuat, gianhap, giaban,
                    thoigianbaohanh, soluong
                );

                result.add(sp);
            }

        } catch (Exception e) {
            Logger.getLogger(SanPhamDAO.class.getName())
                  .log(Level.SEVERE, "Lỗi load sản phẩm", e);
            e.printStackTrace();
        }

        return result;
    }
    @Override
    public SanPhamDTO selectById(String t) {
        SanPhamDTO result = null;
        try (Connection con = JDBCUtil.getConnection()) {
            String sql = "SELECT MSP, TEN, HINHANH, MNCC, THUONGHIEU, NAMSANXUAT, GIANHAP, GIABAN, THOIGIANBAOHANH FROM SANPHAM WHERE MSP=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, t);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int msp = rs.getInt("MSP");
                    String ten = rs.getString("TEN");
                    String hinhanh = rs.getString("HINHANH");
                    int mncc = rs.getInt("MNCC");
                    String thuonghieu = rs.getString("THUONGHIEU");
                    Integer namsanxuat = rs.getObject("NAMSANXUAT") != null ? rs.getInt("NAMSANXUAT") : null;
                    double gianhap = rs.getDouble("GIANHAP");
                    double giaban = rs.getDouble("GIABAN");
                    int thoigianbaohanh = rs.getInt("THOIGIANBAOHANH");
                    result = new SanPhamDTO(msp, ten, hinhanh, mncc, thuonghieu,
                        namsanxuat, gianhap, giaban, thoigianbaohanh);
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, "Lỗi tìm sản phẩm theo ID", e);
        }
        return result;
    }



    @Override
    public int getAutoIncrement() {
        int result = -1;
        try (Connection con = JDBCUtil.getConnection()) {
            String sql = "SELECT CAST(IDENT_CURRENT('SANPHAM') AS INT) AS AUTO_INCREMENT";
            PreparedStatement pst = con.prepareStatement(sql);
            try (ResultSet rs2 = pst.executeQuery()) {
                if (!rs2.isBeforeFirst()) {
                    System.out.println("No data");
                } else {
                    while (rs2.next()) {
                        result = rs2.getInt("AUTO_INCREMENT");
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }



    public int updateGia(int MSP, int giaxuat) {
        int result = 0;
        try (Connection con = JDBCUtil.getConnection()) {
            String sql = "UPDATE SANPHAM SET GIABAN=? WHERE MSP = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, giaxuat);
            pst.setInt(2, MSP);
            result = pst.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public int getMaxMSP() {
        int maxMSP = -1;
        try (Connection con = JDBCUtil.getConnection()) {
            String sql = "SELECT MAX(MSP) AS maxMSP FROM SANPHAM";
            PreparedStatement pst = con.prepareStatement(sql);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    maxMSP = rs.getInt("maxMSP");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return maxMSP;
    }



    // Phương thức này không còn sử dụng vì MVT đã bị xóa khỏi SanPham
    public ArrayList<SanPhamDTO> getSPByMaLoai(int maLoai) {
        ArrayList<SanPhamDTO> result = new ArrayList<>();
        for (SanPhamDTO sp : selectAll()) {
            if (sp.getMNCC() == maLoai) {
                result.add(sp);
            }
        }
        return result;
    }

    public ArrayList<SanPhamDTO> getSPByMaDonVi(int maDonVi) {
        return new ArrayList<>(); // Trả về danh sách rỗng
    }
}
