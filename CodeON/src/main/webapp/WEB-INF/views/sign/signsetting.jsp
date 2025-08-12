<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<%
    String ctxPath = request.getContextPath();
    //     /myspring
%> 

<jsp:include page="../header/header.jsp" />
<jsp:include page="signsidebar.jsp" />

<link href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css" rel="stylesheet" />
<script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>

<style>
    body, html {
        margin: 0; padding: 0; height: 100%; background: #f5f7fa; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    }

    #maininfo {
        margin-left: 220px; /* 사이드바 */
        margin-top: 70px; /* 헤더 */
        padding: 40px 50px;
        min-height: calc(100vh - 70px - 60px);
        box-sizing: border-box;
        display: flex;
        flex-direction: column;
        align-items: center;
    }

    .section-container {
        background: white;
        max-width: 720px;
        width: 100%;
        margin-bottom: 60px;
        padding: 30px 40px;
        border-radius: 12px;
        box-shadow: 0 8px 20px rgba(0,0,0,0.1);
        transition: box-shadow 0.3s ease;
    }
    .section-container:hover {
        box-shadow: 0 12px 30px rgba(0,0,0,0.15);
    }

    h2.section-title {
        font-weight: 900;
        color: #1a3e72;
        border-bottom: 4px solid #1a3e72;
        padding-bottom: 10px;
        margin-bottom: 30px;
        text-align: center;
        letter-spacing: 1.2px;
    }

    /* 도장 업로드 flex 정렬 */
    .stamp-upload-wrap {
        display: flex;
        align-items: center;
        gap: 40px;
        flex-wrap: wrap;
        justify-content: center;
    }

    .image-dropzone {
        width: 160px;
        height: 160px;
        border-radius: 12px;
        border: 3px dashed #1a3e72;
        background-color: #e9f0ff;
        display: flex;
        justify-content: center;
        align-items: center;
        flex-direction: column;
        cursor: pointer;
        overflow: hidden;
        position: relative;
        transition: background-color 0.25s ease, border-color 0.25s ease;
    }
    .image-dropzone:hover {
        background-color: #d4e3ff;
        border-color: #14315a;
    }
    .image-dropzone.dragover {
        background-color: #bfd4ff;
        border-color: #0f2649;
    }

    #previewImg {
        max-width: 100%;
        max-height: 100%;
        border-radius: 12px;
        display: none;
        box-shadow: 0 4px 15px rgba(26,62,114,0.3);
    }

    .upload-controls {
        flex: 1 1 250px;
        display: flex;
        flex-direction: column;
        justify-content: center;
    }
    #fileName {
        width: 100%;
        font-weight: 600;
        padding: 10px 15px;
        margin-bottom: 20px;
        border-radius: 8px;
        border: 1.8px solid #c4c9d9;
        background-color: #f5f7fa;
        color: #555;
        user-select: none;
    }

    .upload-buttons {
        display: flex;
        gap: 20px;
        flex-wrap: wrap;
    }

    .btn-primary {
        background-color: #1a3e72;
        border: none;
        padding: 12px 28px;
        font-weight: 700;
        border-radius: 10px;
        transition: background-color 0.3s ease;
        flex-grow: 1;
        min-width: 140px;
    }
    .btn-primary:hover {
        background-color: #142c51;
    }

    .btn-danger {
        background-color: #c0392b;
        border: none;
        padding: 12px 28px;
        font-weight: 700;
        border-radius: 10px;
        transition: background-color 0.3s ease;
        flex-grow: 1;
        min-width: 140px;
    }
    .btn-danger:hover {
        background-color: #8e251e;
    }

    /* 결재라인 리스트 */
    #savedApprovalLines ul.list-group {
        max-height: 250px;
        overflow-y: auto;
        border-radius: 8px;
        box-shadow: inset 0 0 10px rgba(0,0,0,0.05);
        margin-bottom: 25px;
    }

    #savedApprovalLines li.list-group-item {
        display: flex;
        justify-content: space-between;
        align-items: center;
        font-weight: 600;
        font-size: 1rem;
        padding: 12px 20px;
        border: none;
        border-bottom: 1px solid #eee;
        transition: background-color 0.2s ease;
        cursor: default;
    }
    #savedApprovalLines li.list-group-item:last-child {
        border-bottom: none;
    }
    #savedApprovalLines li.list-group-item:hover {
        background-color: #f0f6ff;
    }

    #savedApprovalLines .badge-primary {
        background-color: #1a3e72;
        font-weight: 700;
        font-size: 0.9rem;
        padding: 7px 14px;
        border-radius: 12px;
    }

    #savedApprovalLines p.text-muted {
        font-style: italic;
        text-align: center;
        color: #999;
        font-weight: 600;
        margin-bottom: 25px;
    }

    /* 결재라인 입력폼 */
    form[name='approvalLineFrm'] .form-group {
        max-width: 400px;
        margin: 0 auto 20px;
    }
    form[name='approvalLineFrm'] label {
        font-weight: 700;
        margin-bottom: 8px;
        display: block;
        color: #1a3e72;
    }
    form[name='approvalLineFrm'] input.form-control,
    form[name='approvalLineFrm'] select.form-control {
        padding: 10px 15px;
        font-size: 1rem;
        border-radius: 8px;
        border: 1.8px solid #c4c9d9;
        transition: border-color 0.3s ease;
    }
    form[name='approvalLineFrm'] input.form-control:focus,
    form[name='approvalLineFrm'] select.form-control:focus {
        outline: none;
        border-color: #1a3e72;
        box-shadow: 0 0 8px rgba(26,62,114,0.3);
    }

    /* 에러메시지 */
    span.error {
        color: #c0392b;
        font-weight: 700;
        font-size: 0.9rem;
        margin-top: 6px;
        display: none;
        text-align: center;
    }

    /* 동적 결재자 이름 입력폼 */
    #approverFields .form-group {
        max-width: 400px;
        margin: 10px auto 15px;
    }

    /* 버튼 정렬 */
    .text-center {
        display: flex;
        justify-content: center;
        gap: 20px;
        flex-wrap: wrap;
        margin-top: 30px;
    }

    @media(max-width: 640px) {
        .stamp-upload-wrap {
            flex-direction: column;
        }
        .upload-buttons {
            flex-direction: column;
        }
        .btn-primary, .btn-danger {
            min-width: 100%;
        }
    }
</style>

<script>
$(function () {
    function updatePreviewAndFileName(file) {
        if (!(file.type === "image/jpeg" || file.type === "image/png")) {
            alert("jpg 또는 png 파일만 가능합니다.");
            return;
        }
        if (file.size >= 10 * 1024 * 1024) {
            alert("10MB 이상인 이미지는 업로드 불가합니다.");
            return;
        }
        const fileReader = new FileReader();
        fileReader.readAsDataURL(file);
        fileReader.onload = function () {
            $("#previewImg").attr("src", fileReader.result).fadeIn(300);
            $("strong").hide();
        };
        $("#fileName").val(file.name);
        const dataTransfer = new DataTransfer();
        dataTransfer.items.add(file);
        $("input[name='stamp_image']")[0].files = dataTransfer.files;
    }

    $("input[name='stamp_image']").on("change", function (e) {
        const file = e.target.files[0];
        if (file) updatePreviewAndFileName(file);
    });

    $("#fileDrop")
        .on("dragenter dragover", function (e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).addClass("dragover");
        })
        .on("dragleave drop", function (e) {
            e.preventDefault();
            e.stopPropagation();
            $(this).removeClass("dragover");
        })
        .on("drop", function (e) {
            const files = e.originalEvent.dataTransfer.files;
            if (files && files.length > 0) updatePreviewAndFileName(files[0]);
        })
        .on("click", function (e) {
            if (!$(e.target).is("input")) {
                $("input[name='stamp_image']").trigger("click");
            }
        });

    $("select[name='approver_count']").on("change", function () {
        const count = parseInt($(this).val());
        const container = $("#approverFields");
        container.empty();
        if (!isNaN(count)) {
            for (let i = 1; i <= count; i++) {
                container.append(`
                    <div class="form-group">
                        <label>결재자 ${i} 이름<span class="text-danger">*</span></label>
                        <input type="text" name="approver_name_${i}" class="form-control infoData" />
                        <span class="error">필수입력</span>
                    </div>
                `);
            }
        }
    });

    $("form[name='stampFrm'], form[name='approvalLineFrm']").on("submit", function (e) {
        $("span.error").hide();
        let isValid = true;

        $(this).find(".infoData").each(function () {
            if ($(this).val().trim() === "") {
                $(this).next(".error").show();
                isValid = false;
            }
        });

        if (!isValid) {
            e.preventDefault();
        }
    });

    $("form[name='stampFrm'] input[type='reset']").on("click", function () {
        $("span.error").hide();
        $("#previewImg").fadeOut(200).attr("src", "");
        $("#fileName").val("");
        $("strong").show();
    });

    $("form[name='approvalLineFrm'] input[type='reset']").on("click", function () {
        $("span.error").hide();
        $("#approverFields").empty();
        $("select[name='approver_count']").val("");
        $("input.infoData").val("");
    });
});

function stampImageSave() {
    const fileInput = $('input[name="stamp_image"]')[0];
    if (!fileInput.files.length) {
        alert("이미지를 선택하세요.");
        return;
    }

    const file = fileInput.files[0];

    $.ajax({
        url: "<%= ctxPath %>/sign/stampImageSave",
        type: "POST",
        data: file,
        processData: false,
        contentType: "application/octet-stream",
        headers: {
            "file-name": encodeURIComponent(file.name)
        },
        success: function (res) {
            if (res.result === "success") {
                alert("저장이 완료되었습니다.");
                $("#previewImg").attr("src", res.url);
            } else {
                alert("저장에 실패하였습니다.");
            }
        },
        error: function (xhr, status, error) {
            console.error(error);
        }
    });
}


</script>

<div id="maininfo">

    <!-- 도장 이미지 업로드 -->
    <section class="section-container">
        <h2 class="section-title">도장 이미지 업로드</h2>
        <form name="stampFrm" enctype="multipart/form-data" method="post" action="stampRegister.go">
            <div class="stamp-upload-wrap">
                <div id="fileDrop" class="image-dropzone" title="도장 이미지를 클릭하거나 드래그하세요">
				    <c:choose>
				        <c:when test="${not empty loginuser.stampImage}">
				            <img id="previewImg" alt="미리보기 이미지" src="/resources/stamp_upload/${loginuser.stampImage}" />
				        </c:when>
				        <c:otherwise>
				            <strong>도장 이미지를 드래그하거나 클릭하여 선택하세요</strong>
				            <img id="previewImg" alt="미리보기 이미지" style="display:none;" />
				        </c:otherwise>
				    </c:choose>
				    <input type="file" name="stamp_image" class="infoData" accept="image/jpeg,image/png" style="display:none;" />
				</div>

                <div class="upload-controls">
                    <input type="text" id="fileName" class="form-control" readonly placeholder="선택된 이미지 파일 이름" />
                    <div class="upload-buttons">
                        <input type="button" onclick="stampImageSave()" value="도장 이미지 저장" class="btn btn-primary" />
                        <input type="reset" value="취소" class="btn btn-danger" />
                    </div>
                </div>
            </div>
        </form>
    </section>

    <!-- 결재라인 관리 -->
    <section class="section-container">
        <h2 class="section-title">결재라인 관리</h2>

        <div id="savedApprovalLines" class="mb-4">
            <c:choose>
                <c:when test="${not empty approvalLines}">
                    <ul class="list-group">
                        <c:forEach var="line" items="${approvalLines}">
                            <li class="list-group-item">
                                ${line.line_name}
                                <span class="badge badge-primary badge-pill">${line.approver_count}명</span>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    <p class="text-muted">저장된 결재라인이 없습니다.</p>
                </c:otherwise>
            </c:choose>
        </div>

        <form name="approvalLineFrm" method="post" action="approvalLineRegister.go">
            <div class="form-group">
                <label>결재라인 이름<span class="text-danger">*</span></label>
                <input type="text" name="line_name" class="form-control infoData" />
                <span class="error">필수입력</span>
            </div>
            <div class="form-group">
                <label>결재 인원<span class="text-danger">*</span></label>
                <select name="approver_count" class="form-control infoData">
                    <option value="">:::선택하세요:::</option>
                    <option value="1">1명</option>
                    <option value="2">2명</option>
                    <option value="3">3명</option>
                </select>
                <span class="error">필수입력</span>
            </div>

            <div id="approverFields"></div>

            <div class="text-center">
                <input type="submit" value="결재라인 저장" class="btn btn-primary" />
                <input type="reset" value="취소" class="btn btn-danger" />
            </div>
        </form>
    </section>

</div>

<jsp:include page="../footer/footer.jsp" />
