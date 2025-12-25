package constants

import androidx.annotation.DrawableRes
import com.example.common.R

enum class CategoryIcon(
    @param:DrawableRes val iconRes: Int,
    val iconName: String,
    val title: String,
    val parentId: Long? = null
) {
    FOOD(R.drawable.chi_1_an_uong, "chi_1_an_uong", "food"),
    SERVICE(R.drawable.chi_10_dich_vu_sinh_hoat, "chi_10_dich_vu_sinh_hoat", "service"),
    transport(R.drawable.chi_11_di_chuyen, "chi_11_di_chuyen", "transport"),
    kid(R.drawable.chi_2_concai, "chi_2_concai", "kid"),
    clothing(R.drawable.chi_13_trang_phuc, "chi_13_trang_phuc", "clothing"),
    gift(R.drawable.chi_5_hieu_hi, "chi_5_hieu_hi", "gift"),
    health(R.drawable.chi_6_suc_khoe, "chi_6_suc_khoe", "health"),
    home(R.drawable.chi_9_nha_cua, "chi_9_nha_cua", "home"),
    entertain(R.drawable.chi_21_huong_thu, "chi_21_huong_thu", "entertain"),
    bank(R.drawable.chi_14_my_pham, "chi_14_my_pham", "bank"),
    income(R.drawable.chi_tien_ra, "chi_tien_ra", "income"),

    // Food
    SHOPPING(R.drawable.chi_13_di_cho, "chi_13_di_cho", "shopping", 1),
    RESTAURANT(R.drawable.chi_1_an_tiem, "chi_1_an_tiem", "restaurant", 1),
    CAFE(R.drawable.chi_1_cafe, "chi_1_cafe", "cafe", 1),
    BREAKFAST(R.drawable.chi_an_sang, "chi_an_sang", "breakfast", 1),
    LUNCH(R.drawable.chi_an_trua, "chi_an_trua", "lunch", 1),
    DINNER(R.drawable.chi_an_toi, "chi_an_toi", "dinner", 1),

    // Service
    ELECTRICITY(R.drawable.chi_17_dien, "chi_17_dien", "electricity", 2),
    WATER(R.drawable.chi_19_nuoc, "chi_19_nuoc", "water", 2),
    INTERNET(R.drawable.chi_22_internet, "chi_22_internet", "internet", 2),
    PHONE(R.drawable.chi_15_dien_thoai_di_dong, "chi_15_dien_thoai_di_dong", "phone", 2),
    CABLE_PHONE(R.drawable.chi_15_dien_thoai_co_din, "chi_15_dien_thoai_co_din", "cable phone", 2),
    GAS(R.drawable.chi_19_gas, "chi_19_gas", "gas", 2),
    TELEVISION(R.drawable.chi_15_truyen_hinh, "chi_15_truyen_hinh", "television", 2),
    HOUSEMAID(R.drawable.chi_2_nguoi_giup_viec, "chi_2_nguoi_giup_viec", "housemaid", 2),

    // Transport
    GASOLINE(R.drawable.chi_12_xang_xe, "chi_12_xang_xe", "gasoline", 3),
    INSURANCE(R.drawable.chi_3_bao_hiem_xe, "chi_3_bao_hiem_xe", "insurance", 3),
    REPAIR(R.drawable.chi_4_sua_chua, "chi_4_sua_chua", "repair", 3),
    PARK(R.drawable.chi_11_gui_xe, "chi_11_gui_xe", "park", 3),
    WASH(R.drawable.chi_11_rua_xe, "chi_11_rua_xe", "wash", 3),
    TAXI(R.drawable.chi_11_taxi, "chi_11_taxi", "taxi", 3),

    // Kid
    SCHOOL_FEE(R.drawable.chi_5_hoc_phi, "chi_5_hoc_phi", "school_fee", 4),
    BOOK(R.drawable.chi_5_sach_vo, "chi_5_sach_vo", "book", 4),
    MILK(R.drawable.chi_6_sua, "chi_6_sua", "milk", 4),
    TOY(R.drawable.chi_16_do_choi, "chi_16_do_choi", "toy", 4),
    POCKET_MONEY(R.drawable.chi_7_tien_tieu_vat, "chi_7_tien_tieu_vat", "pocket money", 4),

    // Clothing
    CLOTHES(R.drawable.chi_7_tien_tieu_vat, "chi_7_tien_tieu_vat", "clothes", 5),
    SHOES(R.drawable.chi_7_tien_tieu_vat, "chi_7_tien_tieu_vat", "shoes", 5),
    ACCESSORIES(R.drawable.chi_7_tien_tieu_vat, "chi_7_tien_tieu_vat", "accessories", 5),

    // Gift and donation
    FUNERAL(R.drawable.chi_ma_chay, "chi_ma_chay", "funeral", 6),
    CHARITY(R.drawable.chi_5_bieu_tang, "chi_5_bieu_tang", "charity", 6),

    // Health
    DOCTOR(R.drawable.chi_6_kham_chua_benh, "chi_6_kham_chua_benh", "doctor", 7),
    PILL(R.drawable.chi_6_thuoc_men, "chi_6_thuoc_men", "pill", 7),
    SPORT(R.drawable.chi_22_the_thao, "chi_22_the_thao", "sport", 7),

    //Home
    FEATURE(R.drawable.chi_17_mua_sam_do_dac, "chi_17_mua_sam_do_dac", "feature", 8),
    HOUSE_REPAIR(R.drawable.chi_4_sua_chua_nha_cua, "chi_4_sua_chua_nha_cua", "house_repair", 8),
    HOUSE_RENT(R.drawable.chi_8_thue_nha, "chi_8_thue_nha", "house_rent", 8),

    // Entertain
    MUSIC(R.drawable.chi_15_vui_choi_giai_tri, "chi_15_vui_choi_giai_tri", "music", 9),
    TRAVEL(R.drawable.chi_12_du_lich, "chi_12_du_lich", "travel", 9),
    BEAUTY(R.drawable.chi_20_lam_dep, "chi_20_lam_dep", "beauty", 9),
    DOMESTIC(R.drawable.chi_14_my_pham, "chi_14_my_pham", "domestic", 9),

    // Bank
    TRANSFER_FEE(R.drawable.chi_phi_chuyen_khoan, "chi_phi_chuyen_khoan", "transfer fee", 10),

    // Income
    SALARY(R.drawable.thu_luong, "thu_luong", "salary"),
    BONUS(R.drawable.thu_thuong, "thu_thuong", "bonus"),
    INTEREST(R.drawable.thu_tien_lai, "thu_tien_lai", "interest"),
    OTHER(R.drawable.thu_khac, "thu_khac", "other"),
    SAVING_INTEREST(R.drawable.thu_lai_tiet_kiem, "thu_lai_tiet_kiem", "saving interest"),
    INCOME(R.drawable.thu_tien_vao, "thu_tien_vao", "income"),

    // Lend
    LEND(R.drawable.thu_cho_vay, "thu_cho_vay", "Lend"),
    BORROW(R.drawable.thu_di_vay, "thu_di_vay", "Borrow"),
    COLLECT_DEBT(R.drawable.thu_thu_no, "thu_thu_no", "Collecting debts"),
    REPAYMENT(R.drawable.thu_tra_no, "thu_tra_no", "repayment");

    companion object {
        fun fromName(name: String): CategoryIcon {
            return entries.find { it.iconName == name } ?: FOOD
        }
    }
}