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

package uigox.demo.adapter;

import zuo.biao.library.base.BaseAdapter;
import android.app.Activity;
import android.view.ViewGroup;
import uigox.demo.model.User;
import uigox.demo.view.UserView;

/**用户adapter
 * @author Lemon
 */
public class UserAdapter extends BaseAdapter<User, UserView> {
	//	private static final String TAG = "UserAdapter";

	public UserAdapter(Activity context) {
		super(context);
	}

	@Override
	public UserView createView(int position, ViewGroup parent) {
		return new UserView(context);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}
}