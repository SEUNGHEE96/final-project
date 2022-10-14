package WebSocketChatting;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.example.demo.NaverInform;

import ch.qos.logback.core.pattern.parser.Parser;
import final_project.AuctionService;
import member.MemberService;
import product.ProductDTO;

@Controller
public class WebSocketController {
	
	@Autowired
	@Qualifier("memberservice")
	MemberService member_service;
	
	@Autowired
	@Qualifier("chattingservice")
	ChattingService chatting_service;
	
	@Autowired
	@Qualifier("auctionservice")
	AuctionService auction_service;
	
	List<ChattingDTO> roomList = new ArrayList<ChattingDTO>();
	static int roomNumber = 0;
	
	@RequestMapping("/chatting1")
	public ModelAndView chat1(int buyer_num , int seller_num , int product_num) throws IOException {
		ModelAndView mv = new ModelAndView();
		
		String buyer_name = member_service.user_id(buyer_num);
		String seller_name = member_service.user_id(seller_num);
		// 구매자 ID
		
		String a = String.valueOf(buyer_num);
		String b = String.valueOf(seller_num);
		String c = String.valueOf(product_num);
		String d = String.valueOf(((buyer_num*buyer_num)+(seller_num*seller_num)+(product_num*product_num))%10);
		String f = a+b+c+d;
		
		int roomNumber = Integer.parseInt(f);
		// 구매자 , 판매자 , 제품 번호를 이용하여 채팅방을 구분할 고유 번호 생성
		// d는 1 , 2 , 3 에 대하여 각 숫자의 제곱을 더 한 값을 10으로 나눈 나머지
		// 1 + 4 + 9 = 14, 14%10 = 4
		// 즉, roomNumber = 1234
		
		
		int check = chatting_service.countlog(buyer_num, seller_num, roomNumber);
		
		if(check == 0) { // 채팅방 처음 입장
			ChattingDTO chatdto = new ChattingDTO();
			chatdto.setBuyer_num(buyer_num);
			chatdto.setBuyer_name(buyer_name);
			chatdto.setSeller_num(seller_num);
			chatdto.setRoomNumber(roomNumber);
			chatdto.setProduct_num(product_num);
			chatdto.setFileName(roomNumber+".json");
			chatting_service.chattinglog(chatdto);
			// chatting_info 테이블에 insert
			
			chatting_service.createFile(roomNumber);
			// 채팅 내역 저장을 위한 txt 파일 생성
		}
		else { // 기존 채팅방 입장

		}
		
		ProductDTO pdto = auction_service.product_info(product_num);
		mv.addObject("dto",pdto);
		
		mv.addObject("buyer_num",buyer_num);
		mv.addObject("buyer_name",buyer_name);
		
		mv.addObject("seller_num",seller_num);
		mv.addObject("seller_name",seller_name);
		
		mv.addObject("roomNumber",roomNumber);
		
		mv.setViewName("WebSocketChatting/chatting");
		return mv;
	}
	// 구매자의 채팅방 입장
	
	@ResponseBody
	@RequestMapping("/roomchecking")
	public int roomchecking(int seller_num , int product_num) {
		
		int check = chatting_service.countlog2(seller_num, product_num);
		return check;
	}
	

	@RequestMapping("/chatting_list")
	public ModelAndView room(int seller_num , int product_num) {
		ModelAndView mv = new ModelAndView();
		
		List<ChattingDTO> chattinglist = chatting_service.chattinglist(seller_num, product_num);
	
		mv.addObject("chattinglist",chattinglist);
		mv.addObject("seller_num",seller_num);
		mv.addObject("product_num",product_num);
		mv.setViewName("WebSocketChatting/room");

		return mv;
	}
	// 해당 상품에 대한 채팅 목록이 있는 room.jsp로 이동
	
	@RequestMapping("/chatting2")
	public ModelAndView chat2(int product_num , int buyer_num , String buyer_name , int seller_num , int roomNumber ) {
	
		ModelAndView mv = new ModelAndView();
		String seller_name = member_service.user_id(seller_num);
		
		ProductDTO pdto = auction_service.product_info(product_num);
		mv.addObject("dto",pdto);
		
		mv.addObject("buyer_num",buyer_num);
		mv.addObject("buyer_name",buyer_name);
		
		mv.addObject("seller_num",seller_num);
		mv.addObject("seller_name",seller_name);
		
		mv.addObject("roomNumber",roomNumber);
		
		mv.setViewName("WebSocketChatting/chatting");
		return mv;
	}
	// 판매자의 채팅방 입장
	
	private static JSONObject jsonToObjectParser(String jsonStr) {
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		
		try {
			obj = (JSONObject) parser.parse(jsonStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return obj;
	}
}