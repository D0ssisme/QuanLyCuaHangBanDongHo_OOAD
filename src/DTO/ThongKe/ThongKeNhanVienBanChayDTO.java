package DTO.ThongKe;

public class ThongKeNhanVienBanChayDTO {
    private int maNhanVien;
    private String tenNhanVien;
    private String chiNhanh;
    private long tongTienBan;

    public ThongKeNhanVienBanChayDTO(int maNhanVien, String tenNhanVien, String chiNhanh, long tongTienBan) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.chiNhanh = chiNhanh;
        this.tongTienBan = tongTienBan;
    }

    public int getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(int maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public String getChiNhanh() {
        return chiNhanh;
    }

    public void setChiNhanh(String chiNhanh) {
        this.chiNhanh = chiNhanh;
    }

    public long getTongTienBan() {
        return tongTienBan;
    }

    public void setTongTienBan(long tongTienBan) {
        this.tongTienBan = tongTienBan;
    }
}
