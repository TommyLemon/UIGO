/*Copyright ©2016 TommyLemon(https://github.com/TommyLemon)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package uigox.demo.model;

import java.util.List;

import androidx.annotation.NonNull;
import uigox.demo.server.model.BaseModel;

public class CommentItem extends BaseModel {
	private static final long serialVersionUID = -1011007127735372824L;

	private Comment comment;
	private User user;
	private User toUser;
	private List<CommentItem> childList;

	public CommentItem() {
		super();
	}
	public CommentItem(Comment comment) {
		this();
		setComment(comment);
	}

	@NonNull
	public Comment getComment() {
		if (comment == null) {
			comment = new Comment();
		}
		return comment;
	}
	public CommentItem setComment(Comment comment) {
		this.comment = comment;
		return this;
	}

	@NonNull
	public User getUser() {
		if (user == null) {
			user = new User(getComment().getUserId());
		}
		return user;
	}
	public CommentItem setUser(User user) {
		this.user = user;
		return this;
	}
	@NonNull
	public User getToUser() {
		if (toUser == null) {
			toUser = new User();
		}
		return toUser;
	}
	public CommentItem setToUser(User toUser) {
		this.toUser = toUser;
		return this;
	}

	public List<CommentItem> getChildList() {
		return childList;
	}
	public CommentItem setChildList(List<CommentItem> childList) {
		this.childList = childList;
		return this;
	}


	@Override
	public Long getId() {
		return getComment().getId();
	}
	public Long getToId() {
		return getComment().getToId();
	}
	@Override
	public Long getDate() {
		return getComment().getDate();
	}

	public Long getUserId() {
		return getUser().getId();
	}

}