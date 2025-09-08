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
import static apijson.orm.AbstractVerifier.OWNER;
import static apijson.orm.AbstractVerifier.UNKNOWN;

import apijson.MethodAccess;

/**用户隐私信息
 * @author Lemon
 */
@MethodAccess(
		GET = {},
		GETS = {OWNER, ADMIN},
		POST = {UNKNOWN, ADMIN},
		DELETE = {ADMIN}
		)
public class Privacy extends BaseModel {
	private static final long serialVersionUID = 1L;

	public static final int PASSWORD_TYPE_LOGIN = 0;
	public static final int PASSWORD_TYPE_PAY = 1;
	
	private String phone; //手机
	private String password; //登录密码，隐藏字段
	private String payPassword; //支付密码，隐藏字段
	private Double balance;	//余额

	public Privacy() {
		super();
	}

	public Privacy(long id) {
		this();
		setId(id);
	}

	public Privacy(String phone, String password) {
		this();
		setPhone(phone);
		setPassword(password);
	}



	public String getPhone() {
		return phone;
	}
	public Privacy setPhone(String phone) {
		this.phone = phone;
		return this;
	}

	/**get_password会转为password
	 * @return
	 */
	public String get__password() {
		return password;
	}
	public Privacy setPassword(String password) {
		this.password = password;
		return this;
	}

	/**get_PayPassword会转为PayPassword
	 * @return
	 */
	public String get__payPassword() {
		return payPassword;
	}
	public Privacy setPayPassword(String payPassword) {
		this.payPassword = payPassword;
		return this;
	}

	public Double getBalance() {
		return balance;
	}
	public Privacy setBalance(Double balance) {
		this.balance = balance;
		return this;
	}

}
