<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
    String ctxPath = request.getContextPath();
%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>

<jsp:include page="../header/header.jsp" />
<jsp:include page="../admin/adminsidebar.jsp" />
        
<meta charset="UTF-8">
<title>통계 차트</title>

<script src="<%= ctxPath %>/js/jquery-3.7.1.min.js"></script>

<style type="text/css">
	.highcharts-figure,
	.highcharts-data-table table {
	    min-width: 320px;
	    max-width: 800px;
	    margin: 1em auto;
	}
	
	div#chart_container {
	    height: 400px;
	}
	
	.highcharts-data-table table {
	    font-family: Verdana, sans-serif;
	    border-collapse: collapse;
	    border: 1px solid #ebebeb;
	    margin: 10px auto;
	    text-align: center;
	    width: 100%;
	    max-width: 500px;
	}
	
	.highcharts-data-table caption {
	    padding: 1em 0;
	    font-size: 1.2em;
	    color: #555;
	}
	
	.highcharts-data-table th {
	    font-weight: 600;
	    padding: 0.5em;
	}
	
	.highcharts-data-table td,
	.highcharts-data-table th,
	.highcharts-data-table caption {
	    padding: 0.5em;
	}
	
	.highcharts-data-table thead tr,
	.highcharts-data-table tr:nth-child(even) {
	    background: #f8f8f8;
	}
	
	.highcharts-data-table tr:hover {
	    background: #f1f7ff;
	}
	
	input[type="number"] {
	    min-width: 50px;
	}
	
	div#table_container table {width: 100%}
	div#table_container th, div#table_container td {border: solid 1px gray; text-align: center;} 
	div#table_container th {background-color: #595959; color: white;} 
</style>

<script src="<%= ctxPath%>/Highcharts-10.3.1/code/highcharts.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/exporting.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/export-data.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/accessibility.js"></script> 
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/series-label.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/data.js"></script>
<script src="<%= ctxPath%>/Highcharts-10.3.1/code/modules/drilldown.js"></script>

<br><br>
<body class="bg-light">
<div class="container-fluid">
    <div class="row">
        <div class="col-md-10 py-5 px-4 offset-md-2">
            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <h2 class="card-title text-center text-primary fw-bold mb-4">사원 통계정보 (차트)</h2>
                    <div class="d-flex justify-content-center mb-5">
                        <form name="searchFrm" class="form-inline">
                            <select name="searchType" id="searchType" class="form-select">
                                <option value="">통계 선택</option>
                                <option value="deptname">부서별 인원통계</option>
                                <option value="gender">성별 인원통계</option>
                            </select>
                        </form>
                    </div>

                    <div id="chart_container" class="highcharts-figure"></div>
                    <div id="table_container" class="mt-5"></div>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="../footer/footer.jsp" />
</body>

<script type="text/javascript">
$(function(){
   $('select#searchType').change(function(e){
	   func_choice($(e.target).val());
	// $(e.target).val() 은 
	// "" 또는 "deptname" 또는 "gender" 또는 "genderHireYear" 또는 "deptnameGender" 또는 "pageurlUsername" 이다.  
   });
   
   // 문서가 로드 되어지면 "부서별 인원통계" 페이지가 보이도록 한다.
   $('select#searchType').val("deptname").trigger("change");
	   
});// end of $(function(){})------------------------------------


//Function Declaration
function func_choice(searchTypeVal) {
	   
	   switch(searchTypeVal){
	   
	   		case "":     // 통계선택하세요 를 선택한 경우 
	   			$('div#chart_container').empty();
	   			$('div#table_container').empty();
	   			$('div.highcharts-data-table').empty();
		        break;
		        
	   		case "deptname":  // 부서별 인원통계 를 선택한 경우 (pie 차트)

	   		     $.ajax({
	   		    	 url: "<%= ctxPath%>/memberInfo/memberCntByDeptname",
	   		    	 dataType:"json",
	   		    	 success:function(json){
	   		    		console.log(JSON.stringify(json));
	   		    		
	   		    		$('div#chart_container').empty();
	   					$('div#table_container').empty();
	   					$('div.highcharts-data-table').empty();
	   		    		 
	   		    		let resultArr = [];
	   		    		
	   		    		for (let i=0; i<json.length; i++) {
	   		    			let obj;
	   		    			
	   		    			if(i == 0) {
	   		    				obj = {name : json[i].department_name,
	   		    						y : Number(json[i].percentage),
	   		    						sliced: true,
	   		    						selected: true};
	   		    			} else {
	   		    				obj = {name : json[i].department_name,
	   		    						y : Number(json[i].percentage)};
	   		    			} 
	   		    			
	   		    			resultArr.push(obj); // 배열속에 객체넣기
	   		    		}
	   		    		
	   		    		// ============================================ //
	   		    		
	   		    		Highcharts.chart('chart_container', {
	   		   			    chart: {
	   		   			        plotBackgroundColor: null,
	   		   			        plotBorderWidth: null,
	   		   			        plotShadow: false,
	   		   			        type: 'pie'
	   		   			    },
	   		   			    title: {
	   		   			        text: '우리회사 부서별 인원통계'
	   		   			    },
	   		   			    tooltip: {
	   		   			        pointFormat: '{series.name}: <b>{point.percentage:.2f}%</b>'
	   		   			    },
	   		   			    accessibility: {
	   		   			        point: {
	   		   			            valueSuffix: '%'
	   		   			        }
	   		   			    },
	   		   			    plotOptions: {
	   		   			        pie: {
	   		   			            allowPointSelect: true,
	   		   			            cursor: 'pointer',
	   		   			            dataLabels: {
	   		   			                enabled: true,
	   		   			                format: '<b>{point.name}</b>: {point.percentage:.2f} %'
	   		   			            }
	   		   			        }
	   		   			    },
	   		   			    series: [{
	   		   			        name: '인원비율',
	   		   			        colorByPoint: true,
	   		   			        data: resultArr
	   		   			    }]
	   		   			});
	   		    		
	   		    		// ============================================ //
	   		    		
	   		    		let v_html = `<table>
	   		    						<tr>
	   		    							<th>부서명</th>
	   		    							<th>인원수</th>
	   		    							<th>퍼센티지</th>
	   		    						</tr>`;
	   		    						
	   		    		$.each(json, function(index, item) {
	   		    			v_html += `<tr>
	   		    							<td>\${item.department_name}</td>
	   		    							<td>\${item.cnt}</td>
	   		    							<td>\${item.percentage} %</td>
	   		    					   </tr>`;
	   		    		});
	   		    						
	   		    		v_html += `</table>`;
	   		    		  
	   		    		$('div#table_container').html(v_html); 
	   		    		
	   		    	 },
	   		    	 error: function(request, status, error){
					    alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
					 }
	   		     });

	   			 break;
	   			 
				case "gender":    // 성별 인원통계 를 선택한 경우 (pie 차트) 
	   			
	   			$.ajax({
	   				url: "<%= ctxPath%>/memberInfo/memberCntByGender",
	   				dataType: "json",
	   				success:function(json) {
	   		    		// console.log(JSON.stringify(json));
	   		    		
		    			$('div#chart_container').empty();
	   					$('div#table_container').empty();
	   					$('div.highcharts-data-table').empty();
	   		    		 
	   		    		let resultArr = [];
	   		    		
	   		    		
	   		    		for (let i=0; i<json.length; i++) {
	   		    			let obj;
	   		    			
	   		    			if(i == 0) {
	   		    				obj = {name : json[i].department_name,
	   		    						y : Number(json[i].percentage),
	   		    						sliced: true,
	   		    						selected: true};
	   		    			} else {
	   		    				obj = {name : json[i].department_name,
	   		    						y : Number(json[i].percentage)};
	   		    			} 
	   		    			
	   		    			resultArr.push(obj); // 배열속에 객체넣기
	   		    		}
	   					
	   		    		// ============================================ //
	   		    		
	   		    		Highcharts.chart('chart_container', {
	   		   			    chart: {
	   		   			        plotBackgroundColor: null,
	   		   			        plotBorderWidth: null,
	   		   			        plotShadow: false,
	   		   			        type: 'pie'
	   		   			    },
	   		   			    title: {
	   		   			        text: '우리회사 성별 인원통계'
	   		   			    },
	   		   			    tooltip: {
	   		   			        pointFormat: '{series.name}: <b>{point.percentage:.2f}%</b>'
	   		   			    },
	   		   			    accessibility: {
	   		   			        point: {
	   		   			            valueSuffix: '%'
	   		   			        }
	   		   			    },
	   		   			    plotOptions: {
	   		   			        pie: {
	   		   			            allowPointSelect: true,
	   		   			            cursor: 'pointer',
	   		   			            dataLabels: {
	   		   			                enabled: true,
	   		   			                format: '<b>{point.name}</b>: {point.percentage:.2f} %'
	   		   			            }
	   		   			        }
	   		   			    },
	   		   			    series: [{
	   		   			        name: '인원비율',
	   		   			        colorByPoint: true,
	   		   			        data: resultArr
	   		   			    }]
	   		   			});
	   		    		
	   		    		// ============================================ //
	   		    		
	   		    		let v_html = `<table>
	    						<tr>
	    							<th>성별</th>
	    							<th>인원수</th>
	    							<th>퍼센티지</th>
	    						</tr>`;
	    						
			    		$.each(json, function(index, item) {
			    			v_html += `<tr>
			    							<td>\${item.gender}</td>
			    							<td>\${item.cnt}</td>
			    							<td>\${item.percentage}</td>
			    					   </tr>`;
			    		});
			    						
			    		v_html += `</table>`;
			    		  
			    		$('div#table_container').html(v_html); 
	   				}, 
   		    	 	error: function(request, status, error){
   		    	 		alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
					}
	   			});
	   			
	   			 break;
	   }
}
</script>