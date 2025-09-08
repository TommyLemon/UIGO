/*Copyright ©2016 TommyLemon(https://github.com/TommyLemon/APIJSON)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package uigox.demo.server.model;

import static apijson.orm.AbstractVerifier.ADMIN;
import static apijson.orm.AbstractVerifier.CIRCLE;
import static apijson.orm.AbstractVerifier.CONTACT;
import static apijson.orm.AbstractVerifier.LOGIN;
import static apijson.orm.AbstractVerifier.OWNER;

import java.util.List;

import apijson.MethodAccess;

/**动态类
 * @author Lemon
 */
@MethodAccess(
		PUT = {LOGIN, CONTACT, CIRCLE, OWNER, ADMIN}//TODO 还要细分，LOGIN,CONTACT只允许修改praiseUserIdList。数据库加role没用，应该将praiseUserIdList移到Praise表
		)
public class Moment extends BaseModel {
	private static final long serialVersionUID = 1L;

	private String content;
	private List<String> pictureList;
	private List<Long> praiseUserIdList;
	private List<Long> commentIdList;

	public Moment() {
		super();
	}
	public Moment(long id) {
		this();
		setId(id);
	}


	public Moment setUserId(Long userId) {
		super.setUserId(userId);
		return this;
	}
	public String getContent() {
		return content;
	}
	public Moment setContent(String content) {
		this.content = content;
		return this;
	}
	public List<String> getPictureList() {
		return pictureList;
	}
	public Moment setPictureList(List<String> pictureList) {
		this.pictureList = pictureList;
		return this;
	}
	public List<Long> getPraiseUserIdList() {
		return praiseUserIdList;
	}
	public Moment setPraiseUserIdList(List<Long> praiseUserIdList) {
		this.praiseUserIdList = praiseUserIdList;
		return this;
	}
	public List<Long> getCommentIdList() {
		return commentIdList;
	}
	public Moment setCommentIdList(List<Long> commentIdList) {
		this.commentIdList = commentIdList;
		return this;
	}
}
