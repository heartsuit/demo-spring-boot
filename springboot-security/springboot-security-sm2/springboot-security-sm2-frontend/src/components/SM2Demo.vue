<template>
  <div>
    <span>用户名</span><input type="text" v-model="user.username" />
    <span>密码</span><input type="password" v-model="user.password" />
    <input type="submit" v-on:click="login" value="登录" />
  </div>
</template>
<script>
import { defineComponent } from "vue";
import axios from "axios";
import { sm2 } from "sm-crypto";

export default defineComponent({
  name: "SM2Demo",
  setup() {},
  data() {
    return {
      user: { username: "dev", password: "123" },
//       publicKey: `MFkwEwYHKoZIzj0CAQYIKoEcz1UBgi0DQgAEq69oLar0vruQNWO8sA4fui58WM7p
// vbMqYCdW49Evi8sUCQqoNYxO4v4uCwAxSS7ztR2NS0FvunCDNqy1l80EBg==`,
      publicKey: '04abaf682daaf4bebb903563bcb00e1fba2e7c58cee9bdb32a602756e3d12f8bcb14090aa8358c4ee2fe2e0b0031492ef3b51d8d4b416fba708336acb597cd0406'
    };
  },
  mounted() {
    this.login();
  },
  methods: {
    login: function () {
      // 密文前面需要加上04标志位，否则后端解密失败
      let userinfo = {
        username: "04" + sm2.doEncrypt(this.user.username, this.publicKey, 1),
        password: "04" + sm2.doEncrypt(this.user.password, this.publicKey, 1),
      };
      console.log(userinfo);

      axios.post("http://localhost:8000/auth/login", userinfo).then(
        function (res) {
          if (res.status == 200) {
            console.log(res.data);
          } else {
            console.error(res);
          }
        },
        function (res) {
          console.error(res);
        }
      );
    }
  },
});
</script>