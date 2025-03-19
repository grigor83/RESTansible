export class User {
    id!: number;
    username: string | null;
    password: string | null;
    phone: string | null;
    email: string | null;

    constructor(username:string | null, password:string | null, phone:string | null, email:string | null,) {
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.email = email;
    }
}
