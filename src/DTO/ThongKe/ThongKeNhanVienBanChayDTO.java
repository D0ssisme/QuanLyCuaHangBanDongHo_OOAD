package DTO.ThongKe;

public class ThongKeNhanVienBanChayDTO {
    private int maNhanVien;
    private String tenNhanVien;
    private String chiNhanh;
    private int soDonBan;

    public ThongKeNhanVienBanChayDTO(int maNhanVien, String tenNhanVien, String chiNhanh, int soDonBan) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.chiNhanh = chiNhanh;
        this.soDonBan = soDonBan;
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

    public int getSoDonBan() {
        return soDonBan;
    }

    public void setSoDonBan(int soDonBan) {
        this.soDonBan = soDonBan;
    }
}
