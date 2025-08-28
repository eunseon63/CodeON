package com.spring.app.chatting.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.spring.app.chatting.domain.MessageDTO;
import com.spring.app.chatting.domain.Mongo_messageDTO;
import com.spring.app.chatting.service.ChattingMongoOperations;
import com.spring.app.domain.MemberDTO;

import lombok.RequiredArgsConstructor;

// === (#웹채팅관련6) ===
@Component
@RequiredArgsConstructor
public class WebsocketEchoHandler extends TextWebSocketHandler {

	// === 웹소켓서버에 연결한 클라이언트 사용자들을 저장하는 리스트 === 
	private List<WebSocketSession> connectedUsers = new ArrayList<>(); 
		
	// === 웹소켓 서버에 접속시 채팅에 접속한 사용자ID, 성명, 이메일 정보를 보여주기 위해 채팅에 접속한 MemberVO 를 저장하는 List   
    private List<MemberDTO> memberDto_list = new ArrayList<>();
	
    
    // ========= 몽고DB 시작 (#웹채팅관련12)======== //
    private final ChattingMongoOperations chattingMongo;
    private final Mongo_messageDTO document;
    // ========= 몽고DB 끝 ======== //
    
	
    // init-method
    public void init() throws Exception{}
    
    
    // === 클라이언트가 웹소켓서버에 연결했을때의 작업 처리하기 ===
    /*
       afterConnectionEstablished(WebSocketSession wsession) 메소드는 
               클라이언트가 웹소켓서버에 연결이 되어지면 자동으로 실행되는 메소드로서
       WebSocket 연결이 열리고 사용이 준비될 때 호출되어지는(실행되어지는) 메소드이다.
    */
    @Override
    public void afterConnectionEstablished(WebSocketSession wsession) throws Exception {
    	// >>> 파라미터 WebSocketSession wsession 은 웹소켓서버에 접속한 클라이언트 임. <<< 
		
		// 웹소켓서버에 접속한 클라이언트의 IP Address 얻어오기
    	/*
    	  STS 메뉴의 
    	  Run --> Run Configuration 
    	      --> Arguments 탭
    	      --> VM arguments 속에 맨 뒤에
    	      --> 한칸 띄우고 -Djava.net.preferIPv4Stack=true 
    	          을 추가한다.  
    	*/
      // System.out.println("===> 웹채팅확인용 : " + wsession.getId() + " 님이 접속했습니다.");
         //	===> 웹채팅확인용 : 9c0fb080-5701-e5b9-e7ab-80dd8b3e0321 님이 접속했습니다.
    	 //	===> 웹채팅확인용 : ec170fc6-6ae6-a439-1578-19d0f03bc2b3 님이 접속했습니다.
    	 //	===> 웹채팅확인용 : f2d074e6-6dc3-6180-62fb-527288846bc6 님이 접속했습니다.
    	
    //	System.out.println("====> 웹채팅확인용 : " + "연결 컴퓨터명 : " + wsession.getRemoteAddress().getHostName());
      // ====> 웹채팅확인용 : 연결 컴퓨터명 : DESKTOP-0FLM0C6
    	
    //	System.out.println("====> 웹채팅확인용 : " + "연결 컴퓨터명 : " + wsession.getRemoteAddress().getAddress().getHostName());
      // ====> 웹채팅확인용 : 연결 컴퓨터명 : DESKTOP-0FLM0C6
    	
    //	System.out.println("====> 웹채팅확인용 : " + "연결 IP : " + wsession.getRemoteAddress().getAddress().getHostAddress()); 
      // ====> 웹채팅확인용 : 연결 IP : 192.168.10.210
    	
    	connectedUsers.add(wsession);
    	
    	// ==== 웹소켓 서버에 접속시 접속자 명단을 알려주기 위한 것 시작 ==== //
		
		// Spring에서 WebSocket 사용시 먼저 HttpSession에 저장된 값들을 읽어와서 사용하기
    	/*
    	   com.spring.app.config.WebSocketConfiguration 클래스 파일에서
    	   
    	   @Override
    	   public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) 메소드내에
    	   addInterceptors(new HttpSessionHandshakeInterceptor()); 를 추가해주면 WebsocketEchoHandler 클래스를 사용하기 전에 
           먼저 HttpSession에 저장되어진 값들을 읽어 들여, WebsocketEchoHandler 클래스에서 사용할 수 있도록 처리해준다.  
        */
    	
    	String connectingUserName = "「"; // 「 은 자음 ㄴ 을 하면 나온다.
    	
    	for(WebSocketSession webSocketSession : connectedUsers) {
    		
    		Map<String, Object> map = webSocketSession.getAttributes();
    		/*
	     	    webSocketSession.getAttributes(); 은 
	     	    HttpSession에 setAttribute("키",오브젝트); 되어 저장되어진 값들을 읽어오는 것으로써,
	     	    리턴값은  "키",오브젝트로 이루어진 Map<String, Object> 으로 받아온다.
	     	*/
    		
    		MemberDTO loginuser = (MemberDTO) map.get("loginuser");
    		// "loginuser" 은 HttpSession에 저장된 키 값으로 로그인 되어진 사용자이다.
    		
    		connectingUserName += loginuser.getMemberName()+" "; 
    		
    		// === 웹소켓 서버에 접속시 채팅에 접속한 사용자ID, 성명, 이메일 정보를 보여주기 위한 것 시작 === //
    		if(memberDto_list.size() > 0) {
    			boolean isExists = false;
    			for(MemberDTO mbrdto : memberDto_list) {
    				if(mbrdto.getMemberUserid().equals(loginuser.getMemberUserid())) {
    					isExists = true;
    					break;
    				}
    			}// end of for-----------------------
    			if(!isExists) {
    				memberDto_list.add(loginuser);
    			}
    		}
    		else {
    			memberDto_list.add(loginuser);
    		}
    		// === 웹소켓 서버에 접속시 채팅에 접속한 사용자ID, 성명, 이메일 정보를 보여주기 위한 것 끝 === //
    		
    	}// end of for----------------------------------
    	
    	connectingUserName += "」"; // 」 은 자음 ㄴ 을 하면 나온다.
		
   //	System.out.println("~~~ 확인용 connectingUserName : " + connectingUserName);
     //	~~~ 확인용 connectingUserName : 「엄정화 강감찬 서영학 」
    	
    	for(WebSocketSession webSocketSession : connectedUsers) {
    		webSocketSession.sendMessage(new TextMessage(connectingUserName));
    	}// end of for------------------------------------
    	
    	// ==== 웹소켓 서버에 접속시 접속자 명단을 알려주기 위한 것 끝 ==== //
    	
    	
    	// >>>> 웹소켓 서버에 접속시 채팅에 접속한 사용자ID, 성명, 이메일 정보를 보여주기 위한 것 시작 <<<< //
    	String v_html = "⊆";  // 'ㄷ'에 있는 것임
        if(memberDto_list.size() > 0) {
        	for(MemberDTO memberDto : memberDto_list) {
        		v_html += "<tr>"
        				+ "<td>"+memberDto.getMemberUserid()+"</td>"
        				+ "<td>"+memberDto.getMemberName()+"</td>"
        				+ "<td>"+memberDto.getMemberEmail()+"</td>"
        				+ "</tr>";
        	}
        	for(WebSocketSession webSocketSession : connectedUsers) {
            //	webSocketSession.sendMessage(new TextMessage(v_html));
            }// end of for------------------------
        }
    	// >>>> 웹소켓 서버에 접속시 채팅에 접속한 사용자ID, 성명, 이메일 정보를 보여주기 위한 것 끝 <<<< //
    	
        
        // ================== 몽고DB 시작(#웹채팅관련14) ======================== //
        List<Mongo_messageDTO> list = chattingMongo.listChatting(); // 몽고DB에 저장된 채팅내용(지난대화)을 읽어온다. 
        
        SimpleDateFormat sdfrmt = new SimpleDateFormat("yyyy년 MM월 dd일 E요일", Locale.KOREAN);
        
        if(list != null && list.size() > 0) { // 이전에 나누었던 대화내용이 있다라면 
     	   
     	   for(int i=0; i<list.size(); i++) {
     		   
     		   String str_created = sdfrmt.format(list.get(i).getCreated()); // 대화내용을 나누었던 날짜를 읽어온다. 
     		   
     		/*   
     		   System.out.println(list.get(i).getUserid() + "\n"
     				            + list.get(i).getName() + "\n"       
     				            + list.get(i).getCurrentTime() + "\n"
     				            + list.get(i).getMessage() + "\n"
     				            + list.get(i).getCreated() + "\n"
     				            + str_created + "\n" 
     				            + list.get(i).getCurrentTime() + "\n");
     		*/   
     		   // =================================================== //
     		   
     		   boolean is_newDay = true; // 대화내용의 날짜가 같은 날짜인지 새로운 날짜인지 알기위한 용도임.
     		   
     		   if(i>0 && str_created.equals(sdfrmt.format(list.get(i-1).getCreated())) ) { // 다음번 내용물에 있는 대화를 했던 날짜가 이전 내용물에 있는 대화를 했던 날짜와 같다라면 
     			   is_newDay = false; // 이 대화내용은 새로운 날짜의 대화가 아님을 표시한다.
     		   }
     		   
     		   if(is_newDay) {
     			   wsession.sendMessage(
		    			  new TextMessage("<div style='text-align: center; background-color: #ccc;'>" + str_created + "</div>")  
		    		   ); // 대화를 나누었던 날짜를 배경색을 회색으로 하여 보여주도록 한다.  
     		   }
     		   
     		   Map<String, Object> map = wsession.getAttributes();
  	       	   /*
  	       	      wsession.getAttributes(); 은 
  	       	      HttpSession에 setAttribute("키",오브젝트); 되어 저장되어진 값들을 읽어오는 것으로써,
  	       	      리턴값은  "키",오브젝트로 이루어진 Map<String, Object> 으로 받아온다.
  	       	   */ 
     		   
     		   MemberDTO loginuser = (MemberDTO)map.get("loginuser");  
     	       // "loginuser" 은 HttpSession에 저장된 키 값으로 로그인 되어진 사용자이다.
     		   
     		   if(loginuser.getMemberUserid().equals(list.get(i).getUserid())) { 
     			   // 본인이 작성한 채팅메시지일 경우라면.. 작성자명 없이 노랑배경색에 오른쪽 정렬로 보이게 한다.
		        	  
          		  wsession.sendMessage(
  					  new TextMessage("<div style='background-color: #ffff80; display: inline-block; max-width: 60%; float: right; padding: 7px; border-radius: 15%; word-break: break-all;'>" + list.get(i).getMessage() + "</div> <div style='display: inline-block; float: right; padding: 20px 5px 0 0; font-size: 7pt;'>"+list.get(i).getCurrentTime()+"</div> <div style='clear: both;'>&nbsp;</div>")  
  				  ); 
  		        	  
  	      	   }
  	      	   else { // 다른 사람이 작성한 채팅메시지일 경우라면.. 작성자명이 나오고 흰배경색으로 보이게 한다.
  	      		  
          		  wsession.sendMessage(
      				  new TextMessage("[<span style='font-weight:bold; cursor:pointer;' class='loginuserName'>" +list.get(i).getName()+ "</span>]<br><div style='background-color: white; display: inline-block; max-width: 60%; padding: 7px; border-radius: 15%; word-break: break-all;'>"+ list.get(i).getMessage() +"</div> <div style='display: inline-block; padding: 20px 0 0 5px; font-size: 7pt;'>"+list.get(i).getCurrentTime()+"</div> <div>&nbsp;</div>") 
      			  );
  	      	  }
     	   
     	   }// end of for------------------
        
        }// end of if(list != null && list.size() > 0)---------
        // ================== 몽고DB 끝 ======================== //
    
    }// end of public void afterConnectionEstablished(WebSocketSession wsession) throws Exception---------------- 
    
    
    
    // === 클라이언트가 웹소켓 서버로 메시지를 보냈을때의 Send 이벤트를 처리하기 ===
    /*
       handleTextMessage(WebSocketSession wsession, TextMessage message) 메소드는 
       클라이언트가 웹소켓서버로 메시지를 전송했을 때 자동으로 호출되는(실행되는) 메소드이다.
       첫번째 파라미터  WebSocketSession 은  메시지를 보낸 클라이언트임.
	   두번째 파라미터  TextMessage 은  메시지의 내용임.
     */
	@Override
	public void handleTextMessage(WebSocketSession wsession, TextMessage message) throws Exception {  
		
		// >>> 파라미터 WebSocketSession wsession은  웹소켓서버에 접속한 클라이언트임. <<<
    	// >>> 파라미터 TextMessage message 은 클라이언트 사용자가 웹소켓서버로 보낸 웹소켓 메시지임. <<<
    	
    	// Spring에서 WebSocket 사용시 먼저 HttpSession에 저장된 값들을 읽어와서 사용하기
    	/*
    	   com.spring.app.config.WebSocketConfiguration 클래스 파일에서
    	   
    	   @Override
    	   public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) 메소드내에
    	   addInterceptors(new HttpSessionHandshakeInterceptor()); 를 추가해주면 WebsocketEchoHandler 클래스를 사용하기 전에 
           먼저 HttpSession에 저장되어진 값들을 읽어 들여, WebsocketEchoHandler 클래스에서 사용할 수 있도록 처리해준다.
        */
		
		Map<String, Object> map = wsession.getAttributes();
		/*
     	    wsession.getAttributes(); 은 
     	    HttpSession에 setAttribute("키",오브젝트); 되어 저장되어진 값들을 읽어오는 것으로써,
     	    리턴값은  "키",오브젝트로 이루어진 Map<String, Object> 으로 받아온다.
     	*/
		
		MemberDTO loginuser = (MemberDTO) map.get("loginuser");
		// "loginuser" 은 HttpSession에 저장된 키 값으로 로그인 되어진 사용자이다.
		
     //	System.out.println("==> 웹채팅확인용 : 로그인ID => " + loginuser.getUserid()); 
		// ==> 웹채팅확인용 : 로그인ID => seoyh
		// ==> 웹채팅확인용 : 로그인ID => eomjh
		
		MessageDTO messageDto = MessageDTO.convertMessage(message.getPayload());
		/* 
		   파라미터 message 는 클라이언트 사용자가 웹소켓서버로 보낸 웹소켓 메시지임
		   message.getPayload() 은  클라이언트 사용자가 보낸 웹소켓 메시지를 String 타입으로 바꾸어주는 것이다.
		   /myspring/src/main/webapp/WEB-INF/views/mycontent1/chatting/multichat.jsp 파일에서 
		   클라이언트가 보내준 메시지는 JSON 형태를 뛴 문자열(String) 이므로 이 문자열을 Gson을 사용하여 MessageDTO 형태의 객체로 변환시켜서 가져온다. 
		*/
		
	//	System.out.println("~~~ 확인용 messageDto.getMessage() => " + messageDto.getMessage()); 
		// ~~~ 확인용 messageDto.getMessage() => 채팅방에 <span style='color: red;'>입장</span> 했습니다. 
			
	//	System.out.println("~~~ 확인용 messageDto.getType() => " + messageDto.getType());
		// ~~~ 확인용 messageDto.getType() => all
			
	//	System.out.println("~~~ 확인용 messageDto.getTo() => " + messageDto.getTo());
		// ~~~ 확인용 messageDto.getTo() => all
		
		Date now = new Date(); // 현재시각 
        String currentTime = String.format("%tp %tl:%tM",now,now,now); 
        // %tp              오전, 오후를 출력
        // %tl              시간을 1~12 으로 출력
		// %tM              분을 00~59 으로 출력
        
        for(WebSocketSession webSocketSession : connectedUsers) {
    		
        	if("all".equals(messageDto.getType())) {
        		// 채팅할 대상이 "전체"인 공개대화인 경우 
        		// 메시지를 자기자신을 뺀 나머지 모든 사용자들에게 메시지를 보냄.
        		
        		if( !wsession.getId().equals(webSocketSession.getId()) ) {
        			// wsession 은 메시지를 보낸 클라이언트임.
                	// webSocketSession 은 웹소켓서버에 연결된 모든 클라이언트중 하나임.
                	// wsession.getId() 와  webSocketSession.getId() 는 자동증가되는 고유한 값으로 나옴 
        	
        			webSocketSession.sendMessage(
        					new TextMessage("<span style='display:none;'>"+wsession.getId()+"</span>&nbsp;[<span style='font-weight:bold; cursor:pointer;' class='loginuserName'>" +loginuser.getMemberName()+ "</span>]<br><div style='background-color: white; display: inline-block; max-width: 60%; padding: 7px; border-radius: 15%; word-break: break-all;'>"+ messageDto.getMessage() +"</div> <div style='display: inline-block; padding: 20px 0 0 5px; font-size: 7pt;'>"+currentTime+"</div> <div>&nbsp;</div>")); 
        			                                                                                                                                                                                                                                                                                                               /* word-break: break-all; 은 공백없이 영어로만 되어질 경우 해당구역을 빠져나가므로 이것을 막기위해서 사용한다. */
        		}
        	}
        	
        	else {
        		// 채팅할 대상이 "전체"가 아닌 특정대상(귀속말대상웹소켓.getId()임)인 귓속말 채팅인 경우
        		
        		String ws_id = webSocketSession.getId();
        		            // webSocketSession 은 웹소켓서버에 연결한 모든 클라이언트중 하나이며, 그 클라이언트의 웹소켓의 고유한 id 값을 알아오는 것임.  
        		
        		if(messageDto.getTo().equals(ws_id) ) {
        		// messageDto.getTo() 는 클라이언트가 보내온 귓속말대상웹소켓.getId() 임.
        			webSocketSession.sendMessage(
        					new TextMessage("<span style='display:none;'>"+wsession.getId()+"</span>&nbsp;[<span style='font-weight:bold; cursor:pointer;' class='loginuserName'>" +loginuser.getMemberName()+ "</span>]<br><div style='background-color: white; display: inline-block; max-width: 60%; padding: 7px; border-radius: 15%; word-break: break-all; color: red;'>"+ messageDto.getMessage() +"</div> <div style='display: inline-block; padding: 20px 0 0 5px; font-size: 7pt;'>"+currentTime+"</div> <div>&nbsp;</div>")); 
        			                                                                                                                                                                                                                                                                                                             /* word-break: break-all; 은 공백없이 영어로만 되어질 경우 해당구역을 빠져나가므로 이것을 막기위해서 사용한다. */
        		    break; // 지금의 특정대상(지금은 귓속말대상 웹소켓id)은 1개이므로 
                           // 특정대상(지금은 귓속말대상 웹소켓id 임)에게만 메시지를 보내고  break;를 한다.
        		}
        		
        	}
        	
    	}// end of for------------------------------------
        
        
        // ================== 몽고DB 시작(#웹채팅관련13) ======================== //
        // === 상대방에게 대화한 내용을 위에서 보여준 후, 채팅할 대상이 "전체" 인 공개대화에 대해서만 몽고DB에 저장하도록 한다. 귓속말은 몽고DB에 저장하지 않도록 한다. === //  
        if("all".equals(messageDto.getType())) {
        
	        /*  === UUID (Universally Unique Identifier) 를 사용한 방법 === 
			      -> 충돌가능성이 0 인 고유 ID를 생성해줌. 
			      -> 업로드 되어질 파일명을 만들 때 많이 사용함.
		    */
	        
	         //	UUID.randomUUID().toString();
			 // UUID.randomUUID() 은 무작위로 생성되는 128bit 식별자이다. 예> f47ac10b-58cc-4372-a567-0e02b2c3d479
			 // UUID.randomUUID().toString() 은 문자열로 변환해주는 것으로 xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx 형식으로 되어진다. 
	        
	         // === 만약에 날짜를 포함하고 싶다면 날짜 + UUID 조합도 가능하다.
			 String str_now_date = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			 String uuid = UUID.randomUUID().toString().replace("-", "");
			 String _id = str_now_date + "_" + uuid; 
	        
			 document.set_id(_id);
			 document.setMessage(messageDto.getMessage());
			 document.setTo(messageDto.getTo());
			 
			 document.setUserid(loginuser.getMemberUserid());
			 document.setName(loginuser.getMemberName());
			 document.setCurrentTime(currentTime);
			 document.setCreated(new Date());
	        
			 chattingMongo.insertMessage(document);
        }
        // ================== 몽고DB 끝 ======================== //
		
	}// end of public void handleTextMessage(WebSocketSession wsession, TextMessage message) throws Exception--------------
    
	
	
	// === 클라이언트가 웹소켓서버와의 연결을 끊을때 작업 처리하기 ===
	/*
	   afterConnectionClosed(WebSocketSession session, CloseStatus status) 메소드는 
	      클라이언트가 연결을 끊었을 때 
	      즉, WebSocket 연결이 닫혔을 때(채팅페이지가 닫히거나 채팅페이지에서 다른 페이지로 이동되는 경우) 자동으로 호출되어지는(실행되어지는) 메소드이다.
	*/
    @Override
    public void afterConnectionClosed(WebSocketSession wsession, CloseStatus status) throws Exception {
    	// 파라미터 WebSocketSession wsession 은 연결을 끊은 웹소켓 클라이언트임.
	    // 파라미터 CloseStatus 은 웹소켓 클라이언트의 연결 상태.
    	
    	Map<String, Object> map = wsession.getAttributes(); 
    	MemberDTO loginuser = (MemberDTO)map.get("loginuser");
    	
    	for (WebSocketSession webSocketSession : connectedUsers) {
        	
        	// 퇴장했다라는 메시지를 자기자신을 뺀 나머지 모든 사용자들에게 메시지를 보내도록 한다.
        	if (!wsession.getId().equals(webSocketSession.getId())) { 
                webSocketSession.sendMessage(
                	new TextMessage("[<span style='font-weight:bold;'>" +loginuser.getMemberName()+ "</span>]님이 <span style='color: red;'>퇴장</span>했습니다.")     
                ); 
            }
        }// end of for------------------------------------------
    	
     // System.out.println("====> 웹채팅확인용 : 웹세션ID " + wsession.getId() + "이 퇴장했습니다.");

    	connectedUsers.remove(wsession);
    	// 웹소켓 서버에 연결되어진 클라이언트 목록에서 연결은 끊은 클라이언트는 삭제시킨다.
    	
    	// ===== 접속을 끊을시 현재 남아있는 접속자명단을 알려주기 위한 것 시작 ===== //
    	String connectingUserName = "「"; // 「 은 자음 ㄴ 을 하면 나온다.
    	
    	for(WebSocketSession webSocketSession : connectedUsers) {
    		
    		Map<String, Object> map2 = webSocketSession.getAttributes();
    		MemberDTO loginuser2 = (MemberDTO) map2.get("loginuser");
    		// "loginuser" 은 HttpSession에 저장된 키 값으로 로그인 되어진 사용자이다.
    		
    		connectingUserName += loginuser2.getMemberName()+" "; 
    	}// end of for----------------------------------
    	
    	connectingUserName += "」"; // 」 은 자음 ㄴ 을 하면 나온다.    	
    	
        for (WebSocketSession webSocketSession : connectedUsers) {
             webSocketSession.sendMessage(new TextMessage(connectingUserName));     
        }// end of for------------------------------------------
        // ===== 접속을 끊을시 현재 남아있는 접속자명단을 알려주기 위한 것 끝 ===== //
        
        
        // >>>> 접속을 끊을시 현재 남아있는 채팅에 접속한 사용자ID, 성명, 이메일 정보를 보여주기 위한 것 시작 <<<< // 
     	if(memberDto_list.size() > 0) {
             	for(MemberDTO memberDto : memberDto_list) {
             		if(memberDto.getMemberUserid().equals(loginuser.getMemberUserid())) {
             			memberDto_list.remove(memberDto);
             			break;
             		}
             	}
             	
             	String v_html = "⊆";  // 'ㄷ'에 있는 것임
                 if(memberDto_list.size() > 0) {
                 	for(MemberDTO memberDto : memberDto_list) {
                 		v_html += "<tr>"
                 				+ "<td>"+memberDto.getMemberUserid()+"</td>"
                 				+ "<td>"+memberDto.getMemberName()+"</td>"
                 				+ "<td>"+memberDto.getMemberEmail()+"</td>"
                 				+ "</tr>";
                 	}
                 	for(WebSocketSession webSocketSession : connectedUsers) {
                     	webSocketSession.sendMessage(new TextMessage(v_html));
                     }// end of for------------------------
                 }
             }
     		// >>>> 접속을 끊을시 현재 남아있는 채팅에 접속한 사용자ID, 성명, 이메일 정보를 보여주기 위한 것 끝 <<<< //
        
    }// end of public void afterConnectionClosed(WebSocketSession wsession, CloseStatus status) throws Exception 
	    
}





