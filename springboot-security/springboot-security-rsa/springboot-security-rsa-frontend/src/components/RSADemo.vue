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
import JSEncrypt from "jsencrypt";

export default defineComponent({
  name: "RSADemo",
  setup() {},
  data() {
    return {
      user: { username: "dev", password: 123 },
      publicKey: `MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC/yhacWdmSauP3/NaDNi1/2l9Z
ZTy+kpRW5OaQcNRErkA1a3APE+IK3axaBGOk9pjUADIrs0Sn2JEtZUMdQvi9B67q
jj4RcAy+68aw0FPvIP7awr/g0KDAGyHLXqoMS0K7zGbNTriaytF6RqeAmu1sX7pP
B1Mp40PX4VyI1s1k4wIDAQAB`,
    };
  },
  mounted() {
    this.login();
  },
  methods: {
    login: function () {
      let userinfo = {
        username: this.encrypt(this.user.username),
        password: this.encrypt(this.user.password),
      };

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
    },
    encrypt: function (str) {
      let jsEncrypt = new JSEncrypt();
      // 设置加密公钥，应通过后端接口获取，这里写在前端代码中
      jsEncrypt.setPublicKey(this.publicKey);
      let encrypted = jsEncrypt.encrypt(str.toString());
      return encrypted;
    },
  },
});
</script>