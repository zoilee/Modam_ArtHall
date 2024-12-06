document.addEventListener("DOMContentLoaded", () => {
    const userIcon = document.getElementById("userIcon");
    const dropdownMenu = document.getElementById("dropdownMenu");
    const adminIcon = document.getElementById("adminIcon");
    const adminDropdownMenu = document.getElementById("adminDropdownMenu");
    const menu = document.querySelector(".menu"); // PC 메뉴

    // 화면 크기 변경 시 드롭다운 및 메뉴 상태 조정
    const handleResize = () => {
        if (window.innerWidth > 768) {
            // 큰 화면에서는 PC 메뉴 보이고, 드롭다운 숨김
            menu.style.display = "flex";
            if (dropdownMenu) dropdownMenu.style.display = "none";
            if (adminDropdownMenu) adminDropdownMenu.style.display = "none";
        } else {
            // 작은 화면에서는 PC 메뉴 숨김
            menu.style.display = "none";
        }
    };

    // 초기 화면 크기 상태 설정
    handleResize();
    window.addEventListener("resize", handleResize);

    // 아이콘 클릭 시 드롭다운 표시/숨김 토글
    if (userIcon && dropdownMenu) {
        userIcon.addEventListener("click", () => {
            dropdownMenu.style.display =
                dropdownMenu.style.display === "flex" ? "none" : "flex";
        });
    }

    if (adminIcon && adminDropdownMenu) {
        adminIcon.addEventListener("click", () => {
            adminDropdownMenu.style.display =
                adminDropdownMenu.style.display === "flex" ? "none" : "flex";
        });
    }
});