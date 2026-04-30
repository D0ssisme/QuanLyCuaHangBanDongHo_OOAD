package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import DTO.SanPhamDTO;
import config.JDBCUtil;

public class SanPhamDAO implements DAOinterface<SanPhamDTO> {

    public static SanPhamDAO getInstance() {
        return new SanPhamDAO();
    }

    @Override
    public int insert(SanPhamDTO t) {
        int result = 0;
        try {
            Connection con = (Connection) JDBCUtil.getConnection();
            String sql = "INSERT INTO SANPHAM (TEN, HINHANH, MNCC, MVT, THUONGHIEU, NAMSANXUAT, GIANHAP, GIABAN, THOIGIANBAOHANH, TT) VALUES (?,?,?,?,?,?,?,?,?,1)";
            PreparedStatement pst = (PreparedStatement) con.prepareStatement(sql);
            pst.setString(1, t.getTEN());
            pst.setString(2, t.getHINHANH());
            pst.setInt(3, t.getMNCC());
            if (t.getMVT() != null) {
                pst.setInt(4, t.getMVT());
            } else {
                pst.setNull(4, java.sql.Types.INTEGER);
            }
            pst.setString(5, t.getTHUONGHIEU());
            if (t.getNAMSANXUAT() != null) {
                pst.setInt(6, t.getNAMSANXUAT());
            } else {
                pst.setNull(6, java.sql.Types.INTEGER);
            }
            pst.setDouble(7, t.getGIANHAP());
            pst.setDouble(8, t.getGIABAN());
            pst.setInt(9, t.getTHOIGIANBAOHANH());
            result = pst.executeUpdate();
            JDBCUtil.closeConnection(con);
        } catch (SQLException ex) {
            Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public int update(SanPhamDTO t) {
        int result = 0;
        try {
            Connection con = (Connection) JDBCUtil.getConnection();
            String sql = "UPDATE SANPHAM SET TEN = ?, HINHANH = ?, MNCC = ?, MVT = ?, THUONGHIEU = ?, NAMSANXUAT = ?, GIANHAP = ?, GIABAN = ?, THOIGIANBAOHANH = ? WHERE MSP=?";
            PreparedStatement pst = (PreparedStatement) con.prepareStatement(sql);
            pst.setString(1, t.getTEN());
            pst.setString(2, t.getHINHANH());
            pst.setInt(3, t.getMNCC());
            if (t.getMVT() != null) {
                pst.setInt(4, t.getMVT());
            } else {
                pst.setNull(4, java.sql.Types.INTEGER);
            }
            pst.setString(5, t.getTHUONGHIEU());
            if (t.getNAMSANXUAT() != null) {
                pst.setInt(6, t.getNAMSANXUAT());
            } else {
                pst.setNull(6, java.sql.Types.INTEGER);
            }
            pst.setDouble(7, t.getGIANHAP());
            pst.setDouble(8, t.getGIABAN());
            pst.setInt(9, t.getTHOIGIANBAOHANH());
            pst.setInt(10, t.getMSP());

            result = pst.executeUpdate();
            JDBCUtil.closeConnection(con);
        } catch (SQLException ex) {
            Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public int delete(String t) {
        int result = 0;
        try {
            Connection con = (Connection) JDBCUtil.getConnection();
            String sql = "UPDATE SANPHAM SET TRANGTHAI = 0 WHERE MSP = ?";
            PreparedStatement pst = (PreparedStatement) con.prepareStatement(sql);
            pst.setString(1, t);
            result = pst.executeUpdate();
            JDBCUtil.closeConnection(con);
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
        ArrayList<SanPhamDTO> result = new ArrayList<SanPhamDTO>();
        try {
            Connection con = (Connection) JDBCUtil.getConnection();
            String sql;
            PreparedStatement pst;
            
            if (mcn != null && !mcn.isEmpty()) {
                // Load tất cả sản phẩm, nhưng chỉ lấy số lượng từ kho của chi nhánh
                sql = "SELECT sp.MSP, sp.TEN, sp.HINHANH, sp.MNCC, sp.THUONGHIEU, sp.NAMSANXUAT, " +
                      "sp.GIANHAP, sp.GIABAN, sp.THOIGIANBAOHANH, COALESCE(tk.SOLUONG, 0) as SOLUONG " +
                      "FROM SANPHAM sp " +
                      "LEFT JOIN TONKHO tk ON sp.MSP = tk.MSP AND tk.MKHO IN (SELECT MKHO FROM KHO WHERE MCN = ?) " +
                      "WHERE sp.TT = 1";
                pst = (PreparedStatement) con.prepareStatement(sql);
                pst.setString(1, mcn);
            } else {
                // Nếu không có MCN, lấy tất cả sản phẩm với tổng số lượng từ tất cả kho
                sql = "SELECT sp.MSP, sp.TEN, sp.HINHANH, sp.MNCC, sp.THUONGHIEU, sp.NAMSANXUAT, " +
                      "sp.GIANHAP, sp.GIABAN, sp.THOIGIANBAOHANH, COALESCE(SUM(tk.SOLUONG), 0) as SOLUONG " +
                      "FROM SANPHAM sp " +
                      "LEFT JOIN TONKHO tk ON sp.MSP = tk.MSP " +
                      "WHERE sp.TT = 1 " +
                      "GROUP BY sp.MSP, sp.TEN, sp.HINHANH, sp.MNCC, sp.THUONGHIEU, sp.NAMSANXUAT, " +
                      "sp.GIANHAP, sp.GIABAN, sp.THOIGIANBAOHANH";
                pst = (PreparedStatement) con.prepareStatement(sql);
            }
            
            ResultSet rs = (ResultSet) pst.executeQuery();
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
                int soluong = rs.getInt("SOLUONG");
                SanPhamDTO sp = new SanPhamDTO(msp, ten, hinhanh, mncc, null, thuonghieu, 
                    namsanxuat, gianhap, giaban, thoigianbaohanh, soluong);
                result.add(sp);
            }
            JDBCUtil.closeConnection(con);
        } catch (Exception e) {
            Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, "Lỗi load danh sách sản phẩm", e);
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public SanPhamDTO selectById(String t) {
        SanPhamDTO result = null;
        try {
            Connection con = (Connection) JDBCUtil.getConnection();
            String sql = "SELECT MSP, TEN, HINHANH, MNCC, THUONGHIEU, NAMSANXUAT, GIANHAP, GIABAN, THOIGIANBAOHANH FROM SANPHAM WHERE MSP=?";
            PreparedStatement pst = (PreparedStatement) con.prepareStatement(sql);
            pst.setString(1, t);
            ResultSet rs = (ResultSet) pst.executeQuery();
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
                result = new SanPhamDTO(msp, ten, hinhanh, mncc, null, thuonghieu, 
                    namsanxuat, gianhap, giaban, thoigianbaohanh);
            }
            JDBCUtil.closeConnection(con);
        } catch (SQLException e) {
            Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, "Lỗi tìm sản phẩm theo ID", e);
        }
        return result;
    }



    @Override
    public int getAutoIncrement() {
        int result = -1;
        try {
            Connection con = (Connection) JDBCUtil.getConnection();
            String sql = "SELECT CAST(IDENT_CURRENT('SANPHAM') AS INT) AS AUTO_INCREMENT";
            PreparedStatement pst = (PreparedStatement) con.prepareStatement(sql);
            ResultSet rs2 = pst.executeQuery();
            if (!rs2.isBeforeFirst()) {
                System.out.println("No data");
            } else {
                while (rs2.next()) {
                    result = rs2.getInt("AUTO_INCREMENT");

                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }



    public int updateGia(int MSP, int giaxuat) {
        int result = 0;
        try {
            Connection con = (Connection) JDBCUtil.getConnection();
            String sql = "UPDATE SANPHAM SET GIABAN=? WHERE MSP = ?";
            PreparedStatement pst = (PreparedStatement) con.prepareStatement(sql);
            pst.setInt(1, giaxuat);
            pst.setInt(2, MSP);
            result = pst.executeUpdate();
            JDBCUtil.closeConnection(con);
        } catch (SQLException ex) {
            Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    public int getMaxMSP() {
        int maxMSP = -1;
        try {
            Connection con = (Connection) JDBCUtil.getConnection();
            String sql = "SELECT MAX(MSP) AS maxMSP FROM SANPHAM";
            PreparedStatement pst = (PreparedStatement) con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                maxMSP = rs.getInt("maxMSP");
            }
            JDBCUtil.closeConnection(con);
        } catch (SQLException ex) {
            Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return maxMSP;
    }



    public ArrayList<SanPhamDTO> getSPByMaViTri(int maViTri) {
        ArrayList<SanPhamDTO> result = new ArrayList<>();
        try {
            Connection con = JDBCUtil.getConnection();
            String sql = "SELECT MSP, TEN, HINHANH, MNCC, THUONGHIEU, NAMSANXUAT, GIANHAP, GIABAN, THOIGIANBAOHANH FROM SANPHAM WHERE TT = 1";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
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
                SanPhamDTO sp = new SanPhamDTO(msp, ten, hinhanh, mncc, null, thuonghieu, 
                    namsanxuat, gianhap, giaban, thoigianbaohanh);
                result.add(sp);
            }
            JDBCUtil.closeConnection(con);
        } catch (SQLException e) {
            Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    public ArrayList<SanPhamDTO> getSPByMaLoai(int maLoai) {
        ArrayList<SanPhamDTO> result = new ArrayList<>();
        try {
            Connection con = JDBCUtil.getConnection();
            String sql = "SELECT MSP, TEN, HINHANH, MNCC, THUONGHIEU, NAMSANXUAT, GIANHAP, GIABAN, THOIGIANBAOHANH FROM SANPHAM WHERE TT = 1";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
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
                SanPhamDTO sp = new SanPhamDTO(msp, ten, hinhanh, mncc, null, thuonghieu, 
                    namsanxuat, gianhap, giaban, thoigianbaohanh);
                result.add(sp);
            }
            JDBCUtil.closeConnection(con);
        } catch (SQLException e) {
            Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }

    public ArrayList<SanPhamDTO> getSPByMaDonVi(int maDonVi) {
        ArrayList<SanPhamDTO> result = new ArrayList<>();
        try {
            Connection con = JDBCUtil.getConnection();
            String sql = "SELECT MSP, TEN, HINHANH, MNCC, THUONGHIEU, NAMSANXUAT, GIANHAP, GIABAN, THOIGIANBAOHANH FROM SANPHAM WHERE TT = 1";
            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();
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
                SanPhamDTO sp = new SanPhamDTO(msp, ten, hinhanh, mncc, null, thuonghieu, 
                    namsanxuat, gianhap, giaban, thoigianbaohanh);
                result.add(sp);
            }
            JDBCUtil.closeConnection(con);
        } catch (SQLException e) {
            Logger.getLogger(SanPhamDAO.class.getName()).log(Level.SEVERE, null, e);
        }
        return result;
    }
}
