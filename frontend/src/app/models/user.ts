export class User {
    id!: number;
    name: string | null;
    lastname: string | null;
    username: string | null;
    password: string | null;
    phoneNumber: string | null;
    email: string | null;
    expiry: number = -1;

    constructor(name:string | null, lastname:string | null, username:string | null, password:string | null, 
                phoneNumber:string | null, email : string | null) {
        this.name = name;
        this.lastname = lastname;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }
}
