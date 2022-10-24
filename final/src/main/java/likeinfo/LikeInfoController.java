package likeinfo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class LikeInfoController {
	
	@Autowired
	@Qualifier("likeinfoservice")
	LikeInfoService likeinfoservice;

	/**
	 * 찜목록
	 * TODO: 현재 likeinfo-mapping.xml 에러남 확인 후 뿌려줘야함
	 * @param user_num 유저 식별자
	 */
	@GetMapping("/wish/{user_num}")
	public String getMemberWishList(@PathVariable int user_num, Model model) {
		List<LikeInfoDTO> dtos = likeinfoservice.getWishList(user_num);
		model.addAttribute("dtos", dtos);
		return "likeinfo/wishlist";
	}
	
}
